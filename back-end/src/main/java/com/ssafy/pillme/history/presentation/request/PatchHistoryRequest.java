package com.ssafy.pillme.history.presentation.request;

import com.ssafy.pillme.history.domain.item.PatchHistoryItem;
import java.util.List;

public record PatchHistoryRequest(
        List<PatchHistoryItem> modifyList
) {
}
