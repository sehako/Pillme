package com.americanstartup.pillme.dependency.domain.vo;

import com.americanstartup.pillme.dependency.domain.entity.Dependency;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProtectorInfo {
    private Long dependencyId;
    private Long protectorId;
    private String protectorName;

    public static ProtectorInfo of(Dependency dependency) {
        return ProtectorInfo.builder()
                .dependencyId(dependency.getId())
                .protectorId(dependency.getProtector().getId())
                .protectorName(dependency.getProtector().getName())
                .build();
    }
}
