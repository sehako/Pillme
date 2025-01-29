package com.ssafy.pillme.medication.application;

import com.ssafy.pillme.medication.infrastructure.InformationRepository;
import com.ssafy.pillme.medication.infrastructure.ManagementRepository;
import com.ssafy.pillme.medication.presentation.request.MedicationRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagementService {
    private final ManagementRepository managementRepository;
    private final InformationRepository informationRepository;

    public void saveTakingInformation(MedicationRegisterRequest request) {

    }
}
