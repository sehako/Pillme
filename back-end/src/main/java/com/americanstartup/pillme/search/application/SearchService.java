package com.americanstartup.pillme.search.application;

import com.americanstartup.pillme.search.application.response.MedicationSearchResponse;
import com.americanstartup.pillme.search.infrastructure.MedicationElasticRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final MedicationElasticRepository medicationElasticRepository;

    public List<MedicationSearchResponse> searchMedication(String keyword) {
        Pageable pageable = PageRequest.of(0, 10);
        return medicationElasticRepository.findByName(keyword, pageable)
                .stream().map(MedicationSearchResponse::of).collect(Collectors.toList());
    }
}
