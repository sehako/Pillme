package com.americanstartup.pillme.dependency.application.response;

import com.americanstartup.pillme.dependency.domain.entity.Dependency;
import com.americanstartup.pillme.dependency.domain.vo.DependentInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class DependentListResponse {
    // 피보호자 정보
    DependentInfo dependentInfo;

    public static List<DependentListResponse> listOf(List<Dependency> dependencies) {
        return dependencies.stream()
                .map(DependentListResponse::of)
                .collect(Collectors.toList());
    }

    public static DependentListResponse of(Dependency dependency) {
        return DependentListResponse.builder()
                .dependentInfo(DependentInfo.of(dependency))
                .build();
    }
}
