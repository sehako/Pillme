package com.ssafy.pillme.notification.presentation.request;

import java.util.List;

public record NotificationDeleteRequest(
        List<Long> notificationDeleteList
) {
}
