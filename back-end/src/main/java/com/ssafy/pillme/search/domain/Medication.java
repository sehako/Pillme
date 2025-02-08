package com.ssafy.pillme.search.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "medication")
@Getter
public class Medication {
    @Id
    private Long id;
    private String name;
    private String company;
    private String image;
}
