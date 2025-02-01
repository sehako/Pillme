package com.ssafy.pillme.management.application;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.global.code.ErrorCode;
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
import com.ssafy.pillme.management.presentation.request.ChangeTakingInformationRequest;
import com.ssafy.pillme.management.presentation.request.DeleteManagementRequest;
import com.ssafy.pillme.management.presentation.request.MedicationRegisterRequest;
import com.ssafy.pillme.search.domain.Medication;
import com.ssafy.pillme.search.infrastructure.MedicationRepository;
import java.time.LocalDate;
import java.util.List;
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

    public void saveTakingInformation(final MedicationRegisterRequest request) {
        // TODO: 인증을 통한 맴버 객체 생성 완료 후에는 정식 엔티티로 바꿔야 한다.
        Member writer = Member.builder().build();
        Member reader = Member.builder().build();
        Information savedInformation = informationRepository.save(request.toInformation(writer, reader));
        saveManagement(request.takingInformationItems(), savedInformation);
    }

    private void saveManagement(final List<TakingInformationItem> takingList, final Information information) {
        for (TakingInformationItem info : takingList) {
            Medication medication = medicationRepository.findById(info.medicationId())
                    .orElseThrow(() -> new NoMedicationException(ErrorCode.MEDICATION_NOT_FOUND));

            Management management = info.toManagement(medication, information);
            managementRepository.save(management);
        }
    }

    @Transactional(readOnly = true)
    public TakingDetailResponse findByInformationId(final Long id) {
        Information information = informationRepository.findById(id)
                .orElseThrow(() -> new NoInformationException(ErrorCode.INFORMATION_NOT_FOUND));

        return TakingDetailResponse.of(
                information,
                information.getManagements()
                        .stream()
                        .map(TakingInformationItem::from)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> findManagementByDate(final LocalDate localDate) {
        return informationRepository.findByDate(localDate)
                .stream()
                .map(PrescriptionResponse::of)
                .toList();
    }

    public void changeTakingInformation(final Long infoId, final ChangeTakingInformationRequest request) {
        request.medications().forEach(medication -> {
            Management management = managementRepository
                    .findByInformationIdAndMedicationId(infoId, medication.medicationId())
                    .orElseThrow(() -> new NoManagementException(ErrorCode.INFORMATION_NOT_FOUND));

            management.changeTakingInformation(medication);
        });
    }

    public void deleteInformation(final Long infoId) {
        Information information = informationRepository.findByIdAndDeletedIsFalse(infoId)
                .orElseThrow(() -> new NoInformationException(ErrorCode.INFORMATION_NOT_FOUND));

        information.delete();

        for (Management management : information.getManagements()) {
            management.delete();
        }
    }

    public void deleteManagement(final Long infoId, final DeleteManagementRequest request) {
        for (Long medicationId : request.medications()) {
            Management management = managementRepository.findByInformationIdAndMedicationId(medicationId, infoId)
                    .orElseThrow(() -> new NoManagementException(ErrorCode.INFORMATION_NOT_FOUND));

            management.delete();
        }
    }
}
