package com.ssafy.pillme.management.domain.item;

public record ChangeManagementItem(
        Long managementId,
        boolean morning,
        boolean lunch,
        boolean dinner,
        boolean sleep
) {
}
