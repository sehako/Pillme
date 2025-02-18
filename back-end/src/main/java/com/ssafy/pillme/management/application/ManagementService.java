package com.ssafy.pillme.management.application;

import static com.ssafy.pillme.global.code.ErrorCode.INFORMATION_NOT_FOUND;
import static com.ssafy.pillme.global.code.ErrorCode.INVALID_TIME_REQUEST;
import static com.ssafy.pillme.global.code.ErrorCode.MANAGEMENT_NOT_FOUND;
import static com.ssafy.pillme.global.code.ErrorCode.MEMBER_NOT_PROTECTOR;
import static com.ssafy.pillme.global.code.ErrorCode.MEMBER_NOT_READER;
import static com.ssafy.pillme.global.code.ErrorCode.MEMBER_NOT_WRITER;

import com.ssafy.pillme.auth.application.service.AuthService;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.dependency.application.service.DependencyService;
import com.ssafy.pillme.management.application.exception.InvalidTimeSelectException;
import com.ssafy.pillme.management.application.exception.MemberIsNotReaderException;
import com.ssafy.pillme.management.application.exception.MemberIsNotWriterException;
import com.ssafy.pillme.management.application.exception.NoInformationException;
import com.ssafy.pillme.management.application.exception.NoManagementException;
import com.ssafy.pillme.management.application.exception.NotProtectorException;
import com.ssafy.pillme.management.application.response.CurrentTakingPrescriptionResponse;
import com.ssafy.pillme.management.application.response.CurrentTakingResponse;
import com.ssafy.pillme.management.application.response.TakingDetailResponse;
import com.ssafy.pillme.management.application.util.RegistrationStatusCalculator;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.Management;
import com.ssafy.pillme.management.domain.item.ChangeManagementItem;
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
import com.ssafy.pillme.notification.application.service.NotificationService;
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
    private final NotificationService notificationService;
    private final DependencyService dependencyService;

    // 새로운 복약 정보 저장
    public Information saveTakingInformation(
            final TakingInformationRegisterRequest request,
            final Member writer
    ) {
        Member reader = authService.findById(request.reader());

        Information savedInformation = informationRepository.save(request.toInformation(writer, reader));

        // 작성자와 읽는 사람이 의존관계에 있는 경우
        if (!writer.getId().equals(reader.getId())) {
            if (dependencyService.isDependencyExist(writer, reader)) {
                throw new NotProtectorException(MEMBER_NOT_PROTECTOR);
            }
            notificationService.sendTakingInformationNotification(writer, reader, savedInformation.getDiseaseName());
            savedInformation.requested();
        }

        for (TakingSettingItem medication : request.medications()) {
            saveManagement(medication, savedInformation);
        }

        return savedInformation;
    }

    // 기존 처방전에 새로운 복약 정보 저장
    public Information addTakingInformation(
            final Long infoId,
            final AddTakingInformationRequest request,
            final Member member
    ) {
        Information information = informationRepository.findByIdMemberFetchJoin(infoId)
                .orElseThrow(() -> new NoInformationException(INFORMATION_NOT_FOUND));
        // 이 부분 조금 더 고민해봐야 함 (요구사항 명세서에 약물 삭제 요청이 있는데 고려를 안함)
        checkWriterValidation(member, information);
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

    public Information acceptInformationRegisterRequest(
            final Long writerId,
            final Member reader
    ) {
        Member writer = authService.findById(writerId);
        Information requestedInformation = findRequestedInformation(reader.getId());

        requestedInformation.requestComplete();

        notificationService.sendTakingInformationAcceptNotification(reader, writer,
                requestedInformation.getDiseaseName());
        return requestedInformation;
    }

    public void rejectInformationRegisterRequest(
            final Long writerId,
            final Member reader
    ) {
        Member writer = authService.findById(writerId);
        Information requestedInformation = findRequestedInformation(writerId);

        List<Management> requestedManagements = managementRepository.findByInformationIdAndInformationReaderIdAndDeletedIsFalse(
                requestedInformation.getId(), writerId);

        notificationService.sendTakingInformationRejectNotification(reader, writer,
                requestedInformation.getDiseaseName());
        managementRepository.deleteAll(requestedManagements);
        informationRepository.delete(requestedInformation);
    }

    @Transactional(readOnly = true)
    public TakingDetailResponse selectInformation(
            final Long infoId,
            final Long readerId
    ) {
        Information information = informationRepository.findByIdAndReaderIdAndDeletedIsFalse(infoId, readerId)
                .orElseThrow(() -> new NoInformationException(INFORMATION_NOT_FOUND));
        List<Management> managements = managementRepository
                .findByInformationIdAndInformationReaderIdAndDeletedIsFalse(infoId, readerId);

        return TakingDetailResponse.of(
                information,
                managements.stream().map(TakingInformationItem::from)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<CurrentTakingResponse> selectManagementByDate(
            final Long memberId) {
        return managementRepository.findManagementsByInformationDateAndReaderIdFetch(memberId)
                .stream()
                .map(CurrentTakingResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CurrentTakingPrescriptionResponse> selectCurrentTakingPrescription(
            final Long targetId
    ) {
        List<Information> currentInformation = informationRepository
                .findCurrentDateAndReaderId(targetId);
        Member targetMember = authService.findById(targetId);

        return currentInformation
                .stream()
                .map(information -> CurrentTakingPrescriptionResponse
                        .of(information, RegistrationStatusCalculator.calculateStatus(information, targetMember)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TakingInformationItem> selectCurrentTakingInformationList(
            final Member member
    ) {
        List<Management> managements = managementRepository
                .findManagementsByInformationDateAndReaderIdFetch(member.getId());

        return managements.stream()
                .map(TakingInformationItem::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Management> selectYesterdayManagementList() {
        return managementRepository.findYesterdayManagements();
    }

    public TakingDetailResponse changeTakingInformation(
            final Long infoId,
            final ChangeTakingInformationRequest request,
            final Member member) {
        Information information = findInformationById(infoId);

        if (!information.getWriter().getId().equals(member.getId())) {
            throw new MemberIsNotWriterException(INFORMATION_NOT_FOUND);
        }

        List<Management> managements = managementRepository
                .findByInformationIdAndInformationWriterIdAndDeletedIsFalse(infoId, member.getId());

        Map<Long, Management> managementMap = getManagementMap(managements);

        checkWriterValidation(member, managements.get(0).getInformation());

        return TakingDetailResponse.of(
                information,
                changeInformation(managementMap, request.medications()));
    }

    private Map<Long, Management> getManagementMap(List<Management> managements) {
        return managements.stream()
                .collect(Collectors.toMap(
                        Management::getId,
                        management -> management
                ));
    }

    private List<TakingInformationItem> changeInformation(
            Map<Long, Management> managementMap,
            List<ChangeManagementItem> changeManagementItems) {
        List<TakingInformationItem> items = new ArrayList<>();

        changeManagementItems.forEach(changeManagementItem -> {
            Management management = managementMap.get(changeManagementItem.managementId());
            management.changeTakingInformation(changeManagementItem);
            items.add(TakingInformationItem.from(management));
        });

        return items;
    }

    public void checkSingleMedicationTaking(
            final SingleTakingCheckRequest request,
            final Member member
    ) {
        Management management = managementRepository.findById(request.managementId())
                .orElseThrow(() -> new NoManagementException(MANAGEMENT_NOT_FOUND));

        checkReaderValidation(member, management.getInformation());
        checkMedicationTaking(management, request.time());
    }

    public void checkAllMedicationTaking(
            final Long infoId,
            final AllTakingCheckRequest request,
            final Member member
    ) {
        List<Management> managements = managementRepository.
                findManagementsByInformationIdAndReaderIdFetch(infoId, member.getId());

        if (managements.isEmpty()) {
            throw new NoManagementException(MANAGEMENT_NOT_FOUND);
        }

        checkReaderValidation(member, managements.get(0).getInformation());

        managements.forEach(management -> checkMedicationTaking(management, request.time()));
    }

    public void checkCurrentTakingAll(
            final CheckCurrentTakingRequest request,
            final Member member
    ) {
        List<Management> managements = managementRepository
                .findManagementsByInformationDateAndReaderIdFetch(member.getId());

        for (Management management : managements) {
            checkMedicationTaking(management, request.time());
        }
    }

    private void checkMedicationTaking(final Management management, TakingType time) {
        switch (time) {
            case MORNING -> management.toggleMorningTaking();
            case LUNCH -> management.toggleLunchTaking();
            case DINNER -> management.toggleDinnerTaking();
            case SLEEP -> management.toggleSleepTaking();
            default -> throw new InvalidTimeSelectException(INVALID_TIME_REQUEST);
        }
    }

    public boolean deleteManagement(
            final Long infoId,
            final DeleteManagementRequest request,
            final Member member) {
        Information information = findInformationById(infoId);
        Member writer = information.getWriter();

        try {
            checkWriterValidation(member, information);

            List<Long> managementList = request.managementList();
            List<Management> managements = managementRepository.findManagementList(managementList, infoId);

            managements.forEach(Management::delete);

            if (managements.size() == managementList.size()) {
                information.delete();
            }

            return true;
        } catch (MemberIsNotWriterException e) {
            sendDeleteRequestToWriter(information, member, writer);
            return false;
        }
    }

    private void sendDeleteRequestToWriter(
            final Information information,
            final Member reader,
            final Member writer
    ) {
        information.requested();
        notificationService.sendTakingInformationDeleteRequestNotification(reader, writer,
                information.getDiseaseName());
    }

    public void acceptDependentDeleteRequest(
            final Long readerId,
            final Member writer
    ) {
        Information requestedInformation = findRequestedInformation(readerId);

        requestedInformation.delete();

        List<Management> managements = managementRepository
                .findManagementsByInformationIdAndReaderIdFetch(
                        requestedInformation.getId(),
                        readerId
                );

        managements.forEach(Management::delete);
        notificationService
                .sendTakingInformationDeleteAcceptNotification(
                        writer,
                        requestedInformation.getReader(),
                        requestedInformation.getDiseaseName()
                );
    }

    public void rejectDependentDeleteRequest(
            final Long readerId,
            final Member writer
    ) {
        Information requestedInformation = findRequestedInformation(readerId);
        requestedInformation.requestComplete();
        notificationService
                .sendTakingInformationDeleteRejectNotification(
                        writer,
                        requestedInformation.getReader(),
                        requestedInformation.getDiseaseName()
                );
    }

    private void checkWriterValidation(
            final Member member,
            final Information information
    ) {
        if (!information.getWriter().getId().equals(member.getId())) {
            throw new MemberIsNotWriterException(MEMBER_NOT_WRITER);
        }
    }

    private void checkReaderValidation(
            final Member member,
            final Information information
    ) {
        if (!information.getReader().getId().equals(member.getId())) {
            throw new MemberIsNotReaderException(MEMBER_NOT_READER);
        }
    }

    private Information findInformationById(final Long infoId) {
        return informationRepository.findByIdMemberFetchJoin(infoId)
                .orElseThrow(() -> new NoInformationException(INFORMATION_NOT_FOUND));
    }

    private Information findRequestedInformation(final Long memberId) {
        return informationRepository.findUnAcceptedInformation(memberId)
                .orElseThrow(() -> new NoInformationException(INFORMATION_NOT_FOUND));
    }
}
