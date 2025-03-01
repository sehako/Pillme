package com.americanstartup.pillme.history.presentation.request;

import com.americanstartup.pillme.history.domain.item.PatchHistoryItem;
import java.util.List;

public record PatchHistoryRequest(
        List<PatchHistoryItem> modifyList
) {
}
