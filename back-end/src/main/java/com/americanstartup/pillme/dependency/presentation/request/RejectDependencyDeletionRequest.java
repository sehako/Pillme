package com.americanstartup.pillme.dependency.presentation.request;

public record RejectDependencyDeletionRequest(Long senderId, Long dependencyId, Long notificationId) {
}
