package com.ssafy.pillme.management.application;

import static com.ssafy.pillme.global.code.ErrorCode.INFORMATION_NOT_FOUND;
import static com.ssafy.pillme.global.code.ErrorCode.INVALID_MEMBER_REQUEST;
import static com.ssafy.pillme.global.code.ErrorCode.INVALID_TIME_REQUEST;
import static com.ssafy.pillme.global.code.ErrorCode.MANAGEMENT_NOT_FOUND;

import com.ssafy.pillme.auth.application.service.AuthService;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.management.application.exception.InvalidMemberException;
import com.ssafy.pillme.management.application.exception.InvalidTimeSelectException;
import com.ssafy.pillme.management.application.exception.NoInformationException;
import com.ssafy.pillme.management.application.exception.NoManagementException;
import com.ssafy.pillme.management.application.response.CurrentTakingPrescriptionResponse;
import com.ssafy.pillme.management.application.response.CurrentTakingResponse;
import com.ssafy.pillme.management.application.response.TakingDetailResponse;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.Management;
import com.ssafy.pillme.management.domain.item.TakingInformationItem;
import com.ssafy.pillme.management.domain.item.TakingSettingItem;
import com.ssafy.pillme.management.domain.type.TakingType;
import com.ssafy.pillme.management.infrastructure.InformationRepository;
import com.ssafy.pillme.management.infrastructure.ManagementRepository;
import com.ssafy.pillme.management.presentation.request.AddTakingInformationRequest;
import com.ssafy.pillme.management.presentation.request.AllTakingCheckRequest;
import com.ssafy.pillme.management.presentation.request.ChangeTakingInformationRequest;
import com.ssafy.pillme.management.presentation.request.CheckCurrentTakingRequest;
import com.ssafy.pillme.management.presentation.request.DeleteManagementRequest;
import com.ssafy.pillme.management.presentation.request.SingleTakingCheckRequest;
import com.ssafy.pillme.management.presentation.request.TakingInformationRegisterRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ManagementService {
    private final ManagementRepository managementRepository;
    private final InformationRepository informationRepository;
    private final AuthService authService;

    public Information saveTakingInformation(
            final TakingInformationRegisterRequest request,
            final Member writer
    ) {
        Member reader = authService.findById(request.reader());
        Information savedInformation = informationRepository.save(request.toInformation(writer, reader));

        for (TakingSettingItem medication : request.medications()) {
            saveManagement(medication, savedInformation);
        }

        return savedInformation;
    }

    public Information addTakingInformation(
            final Long infoId,
            final AddTakingInformationRequest request,
            final Member member
    ) {
        Information information = findInformationById(infoId);
        checkMemberValidation(member, information);
        saveManagement(request.toItem(), information);
        return information;
    }

    private void saveManagement(
            final TakingSettingItem settingItem,
            final Information information
    ) {
        Management management = settingItem.toManagement(information);
        managementRepository.save(management);
    }

    @Transactional(readOnly = true)
    public TakingDetailResponse selectInformation(
            final Long id,
            final Long readerId
    ) {
        Information information = informationRepository.findByIdAndReaderIdAndDeletedIsFalse(id, readerId)
                .orElseThrow(() -> new NoInformationException(INFORMATION_NOT_FOUND));
        List<Management> managements = managementRepository.findManagementsByInformationIdAndReaderId(id, readerId);

        return TakingDetailResponse.of(
                information,
                managements.stream().map(TakingInformationItem::from)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<CurrentTakingResponse> selectManagementByDate(
            final Long memberId,
            final Member member) {
        return managementRepository
                .findByInformationDateAndMember(
                        LocalDate.now(),
                        memberId == null ? member : authService.findById(memberId)
                )
                .stream()
                .map(CurrentTakingResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CurrentTakingPrescriptionResponse> selectCurrentTakingPrescription(
            final Long targetId
    ) {
        List<Information> currentInformation = informationRepository.findAllByDateAndMemberId(LocalDate.now(),
                targetId);

        return currentInformation
                .stream().map(CurrentTakingPrescriptionResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TakingInformationItem> selectCurrentTakingInformationList(
            final Member member
    ) {
        List<Management> managements = managementRepository.findByInformationDateAndMember(LocalDate.now(), member);

        return managements.stream()
                .map(TakingInformationItem::from)
                .collect(Collectors.toList());
    }

    public TakingDetailResponse changeTakingInformation(
            final Long infoId,
            final ChangeTakingInformationRequest request,
            final Member member) {
        List<Management> managements = managementRepository.findManagementsByInformationIdAndWriterId(infoId,
                member.getId());

        if (managements.isEmpty()) {
            return null;
        }

        Map<Long, Management> managementMap = managements.stream()
                .collect(Collectors.toMap(
                        Management::getId,
                        Management -> Management
                ));

        Information returnInformation = findInformationById(infoId);
        checkMemberValidation(member, managements.get(0).getInformation());

        List<TakingInformationItem> items = new ArrayList<>();

        request.medications().forEach(changeManagementItem ->
        {
            Management management = managementMap.get(changeManagementItem.managementId());
            management.changeTakingInformation(changeManagementItem);
            items.add(TakingInformationItem.from(management));
        });

        return TakingDetailResponse.of(returnInformation, items);
    }

    public void checkSingleMedicationTaking(
            final SingleTakingCheckRequest request,
            final Member member
    ) {
        Management management = managementRepository.findById(request.managementId())
                .orElseThrow(() -> new NoManagementException(MANAGEMENT_NOT_FOUND));

        checkMemberValidation(member, management.getInformation());

        checkMedicationTaking(management, request.time());
    }

    public void checkAllMedicationTaking(
            final Long infoId,
            final AllTakingCheckRequest request,
            final Member member
    ) {
        List<Management> managements = managementRepository.findManagementsByInformationIdAndWriterId(infoId,
                member.getId());

        if (managements.isEmpty()) {
            throw new NoManagementException(MANAGEMENT_NOT_FOUND);
        }
        checkMemberValidation(member, managements.get(0).getInformation());

        managements.forEach(management -> checkMedicationTaking(management, request.time()));
    }

    public void checkCurrentTakingAll(
            final CheckCurrentTakingRequest request,
            final Member member
    ) {
        List<Management> managements = managementRepository.findByInformationDateAndMember(LocalDate.now(), member);

        for (Management management : managements) {
            checkMedicationTaking(management, request.time());
        }
    }

    private void checkMedicationTaking(final Management management, TakingType time) {
        switch (time) {
            case MORNING -> management.checkMorningTaking();
            case LUNCH -> management.checkLunchTaking();
            case DINNER -> management.checkDinnerTaking();
            case SLEEP -> management.checkSleepTaking();
            default -> throw new InvalidTimeSelectException(INVALID_TIME_REQUEST);
        }
    }

    public void deleteManagement(
            final Long infoId,
            final DeleteManagementRequest request,
            final Member member) {
        if (request.managementList().isEmpty()) {
            deleteAll(infoId, member);
            return;
        }

        request.managementList().forEach(managementId -> {
            Management management = managementRepository.findByIdAndInformationId(managementId, infoId)
                    .orElseThrow(() -> new NoManagementException(INFORMATION_NOT_FOUND));

            management.delete();
        });
    }

    private void deleteAll(
            final Long infoId,
            final Member member
    ) {
        Information information = findInformationById(infoId);
        List<Management> managements = managementRepository.findManagementsByInformationIdAndWriterId(infoId,
                member.getId());

        information.delete();
        for (Management management : managements) {
            management.delete();
        }
    }

    private void checkMemberValidation(
            final Member member,
            final Information information
    ) {
        if (!information.getReader().getId().equals(member.getId())) {
            throw new InvalidMemberException(INVALID_MEMBER_REQUEST);
        }
    }

    private Information findInformationById(final Long infoId) {
        return informationRepository.findByIdAndDeletedIsFalse(infoId)
                .orElseThrow(() -> new NoInformationException(INFORMATION_NOT_FOUND));
    }
}
