package com.ssafy.pillme.management.application;

import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.infrastructure.InformationRepository;
import com.ssafy.pillme.management.infrastructure.ManagementRepository;
import com.ssafy.pillme.management.presentation.request.MedicationRegisterRequest;
import com.ssafy.pillme.management.presentation.request.TakingInfoRequest;
import java.util.List;
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
        Information savedInformation = informationRepository.save(request.toInformation());
    }

    private boolean saveManagement(List<TakingInfoRequest> takingList, Information information) {

        for (TakingInfoRequest taking : takingList) {

        }
        return false;
    }
}
