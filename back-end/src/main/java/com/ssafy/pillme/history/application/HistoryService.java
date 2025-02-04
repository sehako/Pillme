package com.ssafy.pillme.history.application;

import static com.ssafy.pillme.global.code.ErrorCode.HISTORY_NOT_FOUND;
import static com.ssafy.pillme.global.code.ErrorCode.MEMBER_NOT_MATCHED;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.history.application.exception.HistoryNotFoundException;
import com.ssafy.pillme.history.application.exception.MemberNotMatchedException;
import com.ssafy.pillme.history.application.response.HistorySearchResponse;
import com.ssafy.pillme.history.domain.History;
import com.ssafy.pillme.history.domain.dto.HistorySearchFilter;
import com.ssafy.pillme.history.infrastructure.HistoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;

    public List<HistorySearchResponse> selectHistoryWithFilter(final HistorySearchFilter filter) {
        List<History> historyByCondition = historyRepository.findHistoryByCondition(filter);

        return historyByCondition.stream()
                .map(HistorySearchResponse::of)
                .collect(Collectors.toList());
    }

    public void selectHistoryByInformationId(
            final Long informationId
    ) {
        List<History> historyList = historyRepository.findHistoryByInformationId(informationId);


    }

    public void deleteHistory(final Long id, final Member member) {
        History history = historyRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new HistoryNotFoundException(HISTORY_NOT_FOUND));
        checkMemberValidation(history, member);
        history.delete();
    }

    private void checkMemberValidation(
            final History history,
            final Member member
    ) {
        if (!history.getMember().getId().equals(member.getId())) {
            throw new MemberNotMatchedException(MEMBER_NOT_MATCHED);
        }
    }
}
