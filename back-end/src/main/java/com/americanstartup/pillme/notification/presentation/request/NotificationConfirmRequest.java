package com.americanstartup.pillme.notification.presentation.request;

import java.util.List;

public record NotificationConfirmRequest(
        List<Long> notificationConfirmList
) {}
