package com.ssafy.pillme.search.infrastructure.elasticsearch;

import com.ssafy.pillme.search.domain.elasticsearch.MedicationElastic;
import java.util.Optional;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MedicationElasticRepository extends ElasticsearchRepository<MedicationElastic, String> {
    Optional<MedicationElastic> findByName(String name);
}
