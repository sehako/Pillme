package com.ssafy.pillme.search.domain.elasticsearch;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;


@Document(indexName = "medications")
@Getter
@ToString
public class MedicationElastic {
    @Id
    private String id;
    private String name;
    private String company;
    private String image;
}
