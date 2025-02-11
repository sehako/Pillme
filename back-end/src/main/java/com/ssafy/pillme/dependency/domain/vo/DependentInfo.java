package com.ssafy.pillme.dependency.domain.vo;

import com.ssafy.pillme.dependency.domain.entity.Dependency;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DependentInfo {
    private Long dependencyId;
    private Long dependentId;
    private String dependentName;

    public static DependentInfo of(Dependency dependency) {
        return DependentInfo.builder()
                .dependencyId(dependency.getId())
                .dependentId(dependency.getDependent().getId())
                .dependentName(dependency.getDependent().getName())
                .build();
    }
}
