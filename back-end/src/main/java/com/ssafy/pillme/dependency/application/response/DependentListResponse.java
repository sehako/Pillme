package com.ssafy.pillme.dependency.application.response;

import com.ssafy.pillme.dependency.domain.entity.Dependency;
import com.ssafy.pillme.dependency.domain.vo.DependentInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class DependentListResponse {
    // 관계 ID
    private Long dependencyId;
    // 피보호자 정보
    DependentInfo dependentInfo;

    public static List<DependentListResponse> listOf(List<Dependency> dependencies) {
        return dependencies.stream()
                .map(DependentListResponse::of)
                .collect(Collectors.toList());
    }

    public static DependentListResponse of(Dependency dependency) {
        return DependentListResponse.builder()
                .dependencyId(dependency.getId())
                .dependentInfo(DependentInfo.of(dependency.getDependent()))
                .build();
    }
}
