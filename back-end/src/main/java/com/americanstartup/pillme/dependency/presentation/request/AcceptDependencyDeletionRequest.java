package com.americanstartup.pillme.dependency.presentation.request;

public record AcceptDependencyDeletionRequest(Long senderId, Long dependencyId, Long notificationId) {
}
