package com.ssafy.pillme.management.application;

import static com.ssafy.pillme.global.code.ErrorCode.INFORMATION_NOT_FOUND;
import static com.ssafy.pillme.global.code.ErrorCode.INVALID_MEMBER_INFO;
import static com.ssafy.pillme.global.code.ErrorCode.INVALID_MEMBER_REQUEST;
import static com.ssafy.pillme.global.code.ErrorCode.INVALID_TIME_REQUEST;
import static com.ssafy.pillme.global.code.ErrorCode.MANAGEMENT_NOT_FOUND;

import com.ssafy.pillme.auth.application.service.AuthService;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.infrastructure.repository.MemberRepository;
import com.ssafy.pillme.global.exception.CommonException;
import com.ssafy.pillme.management.application.exception.InvalidMemberException;
import com.ssafy.pillme.management.application.exception.InvalidTimeSelectException;
import com.ssafy.pillme.management.application.exception.NoInformationException;
import com.ssafy.pillme.management.application.exception.NoManagementException;
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
    private final MemberRepository memberRepository;
    private final AuthService authService;

    public Information saveTakingInformation(
            final TakingInformationRegisterRequest request,
            final Member writer
    ) {
        Member reader = memberRepository.findById(request.reader())
                .orElseThrow(() -> new CommonException(INVALID_MEMBER_INFO));
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
        saveManagement(request.management(), information);
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

        return TakingDetailResponse.of(
                information,
                information.getManagements()
                        .stream()
                        .map(TakingInformationItem::from)
                        .collect(Collectors.toList())
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
        Information information = findInformationById(infoId);

        checkMemberValidation(member, information);

        List<TakingInformationItem> items = new ArrayList<>();

        request.managementList().forEach(managementInfo -> {
            Management management = managementRepository
                    .findByIdAndInformationId(managementInfo.managementId(), infoId)
                    .orElseThrow(() -> new NoManagementException(INFORMATION_NOT_FOUND));

            management.changeTakingInformation(managementInfo);

            items.add(TakingInformationItem.from(management));
        });

        return TakingDetailResponse.of(information, items);
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
        Information information = findInformationById(infoId);
        checkMemberValidation(member, information);

        for (Management management : information.getManagements()) {
            checkMedicationTaking(management, request.time());
        }
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

    public void deleteManagement(final Long infoId, final DeleteManagementRequest request) {
        if (request.managementList().isEmpty()) {
            deleteInformation(infoId);
            return;
        }

        for (Long managementId : request.managementList()) {
            Management management = managementRepository.findByIdAndInformationId(managementId, infoId)
                    .orElseThrow(() -> new NoManagementException(INFORMATION_NOT_FOUND));

            management.delete();
        }
    }

    private void deleteInformation(final Long infoId) {
        Information information = findInformationById(infoId);
        information.delete();

        for (Management management : information.getManagements()) {
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
