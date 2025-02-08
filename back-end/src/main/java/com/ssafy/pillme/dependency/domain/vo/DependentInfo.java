package com.ssafy.pillme.dependency.domain.vo;

import com.ssafy.pillme.auth.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DependentInfo {
    private Long dependentId;
    private String dependentName;

    public static DependentInfo of(Member dependent) {
        return DependentInfo.builder()
                .dependentId(dependent.getId())
                .dependentName(dependent.getName())
                .build();
    }
}
