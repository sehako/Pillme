package com.americanstartup.pillme.management.domain.item;

public record ChangeManagementItem(
        Long managementId,
        Integer period,
        boolean morning,
        boolean lunch,
        boolean dinner,
        boolean sleep
) {
}
