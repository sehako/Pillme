package com.ssafy.pillme.search.infrastructure;

import com.ssafy.pillme.search.domain.MedicationDocument;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MedicationElasticRepository extends ElasticsearchRepository<MedicationDocument, String> {
    List<MedicationDocument> findByName(final String keyword, final Pageable pageable);
}
