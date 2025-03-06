package com.americanstartup.pillme.dependency.application.response;

import com.americanstartup.pillme.dependency.domain.entity.Dependency;
import com.americanstartup.pillme.dependency.domain.vo.DependentInfo;
import com.americanstartup.pillme.dependency.domain.vo.ProtectorInfo;

import java.util.ArrayList;
import java.util.List;

public record RelationShipListResponse(List<DependentInfo> dependentList, List<ProtectorInfo> protectorList) {
    public static RelationShipListResponse of(List<Dependency> dependencies, Long memberId) {
        List<DependentInfo> dependents = new ArrayList<>();
        List<ProtectorInfo> protectors = new ArrayList<>();

        for (Dependency dependency : dependencies) {
            // 현재 로그인한 사용자가 보호자인 경우
            if (dependency.getProtector().getId().equals(memberId)) {
                dependents.add(DependentInfo.of(dependency));
            }
            // 현재 로그인한 사용자가 피보호자인 경우
            else if (dependency.getDependent().getId().equals(memberId)) {
                protectors.add(ProtectorInfo.of(dependency));
            }
        }

        return new RelationShipListResponse(dependents, protectors);
    }
}