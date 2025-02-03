package com.ssafy.pillme.management.application;

import static com.ssafy.pillme.global.code.ErrorCode.INFORMATION_NOT_FOUND;
import static com.ssafy.pillme.global.code.ErrorCode.INVALID_MEMBER_INFO;
import static com.ssafy.pillme.global.code.ErrorCode.MANAGEMENT_NOT_FOUND;
import static com.ssafy.pillme.global.code.ErrorCode.MEDICATION_NOT_FOUND;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.infrastructure.repository.MemberRepository;
import com.ssafy.pillme.global.exception.CommonException;
import com.ssafy.pillme.management.application.exception.NoInformationException;
import com.ssafy.pillme.management.application.exception.NoManagementException;
import com.ssafy.pillme.management.application.exception.NoMedicationException;
import com.ssafy.pillme.management.application.response.PrescriptionResponse;
import com.ssafy.pillme.management.application.response.TakingDetailResponse;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.Management;
import com.ssafy.pillme.management.domain.item.TakingInformationItem;
import com.ssafy.pillme.management.infrastructure.InformationRepository;
import com.ssafy.pillme.management.infrastructure.ManagementRepository;
import com.ssafy.pillme.management.presentation.request.AllTakingCheckRequest;
import com.ssafy.pillme.management.presentation.request.ChangeTakingInformationRequest;
import com.ssafy.pillme.management.presentation.request.DeleteManagementRequest;
import com.ssafy.pillme.management.presentation.request.MedicationRegisterRequest;
import com.ssafy.pillme.management.presentation.request.SingleTakingCheckRequest;
import com.ssafy.pillme.search.domain.Medication;
import com.ssafy.pillme.search.infrastructure.MedicationRepository;
import java.time.LocalDate;
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
    private final MedicationRepository medicationRepository;
    private final MemberRepository memberRepository;

    public Information saveTakingInformation(final MedicationRegisterRequest request, final Member writer) {
        Member reader = memberRepository.findById(request.reader())
                .orElseThrow(() -> new CommonException(INVALID_MEMBER_INFO));
        Information savedInformation = informationRepository.save(request.toInformation(writer, reader));
        saveManagement(request.takingInformationItems(), savedInformation);

        return savedInformation;
    }

    private void saveManagement(final List<TakingInformationItem> takingList, final Information information) {
        for (TakingInformationItem info : takingList) {
            Medication medication = medicationRepository.findById(info.medicationId())
                    .orElseThrow(() -> new NoMedicationException(MEDICATION_NOT_FOUND));

            Management management = info.toManagement(medication, information);
            managementRepository.save(management);
        }
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
    public List<PrescriptionResponse> findManagementByDate(final LocalDate localDate, final Member member) {
        return informationRepository.findByDate(localDate)
                .stream()
                .map(information -> PrescriptionResponse.of(
                        information, member
                ))
                .collect(Collectors.toList());
    }

    public void changeTakingInformation(final Long infoId, final ChangeTakingInformationRequest request) {
        request.medications().forEach(medication -> {
            Management management = managementRepository
                    .findByInformationIdAndMedicationId(infoId, medication.medicationId())
                    .orElseThrow(() -> new NoManagementException(INFORMATION_NOT_FOUND));

            management.changeTakingInformation(medication);
        });
    }

    public void checkSingleMedicationTaking(final Long infoId, final SingleTakingCheckRequest request) {
        Management management = managementRepository.findByInformationIdAndMedicationId(infoId, request.medicationId())
                .orElseThrow(() -> new NoManagementException(MANAGEMENT_NOT_FOUND));

        checkMedicationTaking(management, request.time());
    }

    public void checkAllMedicationTaking(final Long infoId, final AllTakingCheckRequest request) {
        Information information = informationRepository.findById(infoId)
                .orElseThrow(() -> new NoInformationException(INFORMATION_NOT_FOUND));

        for (Management management : information.getManagements()) {
            checkMedicationTaking(management, request.time());
        }
    }

    private void checkMedicationTaking(final Management management, String time) {
        management.checkMedicationTaking(time);
    }

    public void deleteManagement(final Long infoId, final DeleteManagementRequest request) {
        if (request.medications().isEmpty()) {
            deleteInformation(infoId);
            return;
        }

        for (Long medicationId : request.medications()) {
            Management management = managementRepository.findByInformationIdAndMedicationId(medicationId, infoId)
                    .orElseThrow(() -> new NoManagementException(INFORMATION_NOT_FOUND));

            management.delete();
        }
    }

    private void deleteInformation(final Long infoId) {
        Information information = informationRepository.findByIdAndDeletedIsFalse(infoId)
                .orElseThrow(() -> new NoInformationException(INFORMATION_NOT_FOUND));

        information.delete();

        for (Management management : information.getManagements()) {
            management.delete();
        }
    }
}
