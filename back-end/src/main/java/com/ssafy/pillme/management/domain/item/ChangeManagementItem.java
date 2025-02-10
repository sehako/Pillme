package com.ssafy.pillme.management.domain.item;

public record ChangeManagementItem(
        Long managementId,
        Integer period,
        Integer servingSize,
        boolean morning,
        boolean lunch,
        boolean dinner,
        boolean sleep
) {
}
