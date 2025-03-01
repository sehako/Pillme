package com.americanstartup.pillme.management.application;

import static com.americanstartup.pillme.global.code.ErrorCode.ANALYZE_ERROR;
import static com.americanstartup.pillme.global.code.ErrorCode.INFORMATION_NOT_FOUND;
import static com.americanstartup.pillme.global.code.ErrorCode.INVALID_TIME_REQUEST;
import static com.americanstartup.pillme.global.code.ErrorCode.MANAGEMENT_NOT_FOUND;
import static com.americanstartup.pillme.global.code.ErrorCode.MEMBER_NOT_PROTECTOR;
import static com.americanstartup.pillme.global.code.ErrorCode.MEMBER_NOT_READER;
import static com.americanstartup.pillme.global.code.ErrorCode.MEMBER_NOT_WRITER;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

import com.americanstartup.pillme.auth.application.service.AuthService;
import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.dependency.application.service.DependencyService;
import com.americanstartup.pillme.management.application.exception.AnalyzeProcessingException;
import com.americanstartup.pillme.management.application.exception.InvalidTimeSelectException;
import com.americanstartup.pillme.management.application.exception.MemberIsNotReaderException;
import com.americanstartup.pillme.management.application.exception.MemberIsNotWriterException;
import com.americanstartup.pillme.management.application.exception.NoInformationException;
import com.americanstartup.pillme.management.application.exception.NoManagementException;
import com.americanstartup.pillme.management.application.exception.NotProtectorException;
import com.americanstartup.pillme.management.application.response.CurrentTakingResponse;
import com.americanstartup.pillme.management.application.response.TakingDetailResponse;
import com.americanstartup.pillme.management.application.response.TakingPrescriptionResponse;
import com.americanstartup.pillme.management.application.util.RegistrationStatusCalculator;
import com.americanstartup.pillme.management.domain.Information;
import com.americanstartup.pillme.management.domain.Management;
import com.americanstartup.pillme.management.domain.PrescriptionRequestResult;
import com.americanstartup.pillme.management.domain.item.ChangeManagementItem;
import com.americanstartup.pillme.management.domain.item.TakingInformationItem;
import com.americanstartup.pillme.management.domain.item.TakingSettingItem;
import com.americanstartup.pillme.management.domain.type.TakingType;
import com.americanstartup.pillme.management.infrastructure.InformationRepository;
import com.americanstartup.pillme.management.infrastructure.ManagementRepository;
import com.americanstartup.pillme.management.presentation.request.AddTakingInformationRequest;
import com.americanstartup.pillme.management.presentation.request.AllTakingCheckRequest;
import com.americanstartup.pillme.management.presentation.request.AnalyzeImageRequest;
import com.americanstartup.pillme.management.presentation.request.ChangeTakingInformationRequest;
import com.americanstartup.pillme.management.presentation.request.CheckCurrentTakingRequest;
import com.americanstartup.pillme.management.presentation.request.DeleteManagementRequest;
import com.americanstartup.pillme.management.presentation.request.SingleTakingCheckRequest;
import com.americanstartup.pillme.management.presentation.request.TakingInformationRegisterRequest;
import com.americanstartup.pillme.notification.application.service.NotificationService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagementService {
    private final ManagementRepository managementRepository;
    private final InformationRepository informationRepository;
    private final AuthService authService;
    private final NotificationService notificationService;
    private final DependencyService dependencyService;
    private final WebClient.Builder webClientBuilder;

    // fastapi 요청
    public void requestToFastApi(
            final MultipartFile file,
            final AnalyzeImageRequest request,
            final Member writer
    ) {
        WebClient webClient = buildWebClient();

        try {
            // 파일을 임시 디렉토리에 저장
            Path tempFile = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            FileSystemResource fileResource = new FileSystemResource(tempFile.toFile());
            Member reader = authService.findById(request.readerId());

            Mono<List<PrescriptionRequestResult>> analyzeResult = webClient
                    .post()
                    .uri("/prescription")
                    .contentType(MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file", fileResource))
                    .retrieve()
                    .bodyToFlux(PrescriptionRequestResult.class)
                    .collectList();
            processRequestResult(analyzeResult, reader, writer);
        } catch (IOException e) {
            throw new AnalyzeProcessingException(ANALYZE_ERROR);
        }
    }

    private void processRequestResult(
            final Mono<List<PrescriptionRequestResult>> result,
            final Member reader,
            final Member writer
    ) {
        result.subscribe(prescriptionRequestResult -> {
            List<Management> managements = prescriptionRequestResult.stream()
                    .map(PrescriptionRequestResult::toManagement)
                    .toList();

            Information notUsedEntity = Information.builder()
                    .reader(reader)
                    .writer(writer)
                    .diseaseName("NOT_COMPLETED")
                    .build();

            if (!writer.getId().equals(reader.getId())) {
                if (!dependencyService.isDependencyExist(writer, reader)) {
                    throw new NotProtectorException(MEMBER_NOT_PROTECTOR);
                }
                notificationService.sendTakingInformationNotification(writer, reader,
                        notUsedEntity.getDiseaseName());
                notUsedEntity.requested();
                informationRepository.save(notUsedEntity);
                managementRepository.saveAll(managements);

                return;
            }

            informationRepository.save(notUsedEntity);
            managementRepository.saveAll(managements);
        });
    }

    private WebClient buildWebClient() {
        return webClientBuilder
                .baseUrl("https://i12a606.p.ssafy.io")
                .defaultHeaders(
                        httpHeaders ->
                                httpHeaders.add(HttpHeaders.CONTENT_TYPE, "multipart/form-data")
                )
                .build();
    }

    // 새로운 복약 정보 저장
    public Information saveTakingInformation(
            final TakingInformationRegisterRequest request,
            final Member writer
    ) {
        Member reader = authService.findById(request.reader());

        Information information = request.toInformation(writer, reader);
        informationRepository.save(information);

        // 작성자와 읽는 사람이 의존관계에 있는 경우
//        if (!writer.getId().equals(reader.getId())) {
//            if (!dependencyService.isDependencyExist(writer, reader)) {
//                throw new NotProtectorException(MEMBER_NOT_PROTECTOR);
//            }
//            notificationService.sendTakingInformationNotification(writer, reader, information.getDiseaseName());
//            information.requested();
//        }

        for (TakingSettingItem medication : request.medications()) {
            saveManagement(medication, information);
        }

        return information;
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
    public List<TakingPrescriptionResponse> selectTakingPrescription(
            final Long targetId,
            final LocalDate date
    ) {
        List<Information> currentInformation = informationRepository
                .findAllByDate(targetId, date != null ? date : LocalDate.now());
        Member targetMember = authService.findById(targetId);

        return currentInformation
                .stream()
                .map(information -> TakingPrescriptionResponse
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
            checkAllMedicationTaking(management, request.time());
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

    private void checkAllMedicationTaking(final Management management, TakingType time) {
        switch (time) {
            case MORNING -> management.checkMorningTaking();
            case LUNCH -> management.checkLunchTaking();
            case DINNER -> management.checkDinnerTaking();
            case SLEEP -> management.checkSleepTaking();
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
//            checkWriterValidation(member, information);

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
