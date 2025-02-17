package com.ssafy.pillme.search.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Document(indexName = "medication")
@ToString
public class MedicationDocument {
    @Id
    private String id;
    @Field(type = FieldType.Text, analyzer = "suggest_index_analyzer", searchAnalyzer = "suggest_search_analyzer")
    private String name;
    private String company;
    private String image;
}
