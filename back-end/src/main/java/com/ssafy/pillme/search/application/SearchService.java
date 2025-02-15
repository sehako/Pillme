package com.ssafy.pillme.search.application;

import com.ssafy.pillme.search.application.response.MedicationSearchResponse;
import com.ssafy.pillme.search.infrastructure.MedicationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final MedicationRepository medicationRepository;

    public List<MedicationSearchResponse> searchMedication(String keyword) {
        Pageable pageable = PageRequest.of(0, 10);
        return medicationRepository.findByKeyword(keyword, pageable);
    }
}
