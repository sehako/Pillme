package com.ssafy.pillme.member.application.service;

import com.ssafy.pillme.global.util.SecurityUtil;
import com.ssafy.pillme.member.application.exception.AlreadyExistNicknameException;
import com.ssafy.pillme.member.application.exception.NoMemberInfoException;
import com.ssafy.pillme.member.application.exception.SameNicknameException;
import com.ssafy.pillme.member.domain.entity.LoginMember;
import com.ssafy.pillme.member.domain.vo.NicknameValidationResult;
import com.ssafy.pillme.member.infrastructure.repository.LoginMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChangeNicknameService {
    private final LoginMemberRepository loginMemberRepository;

    // 닉네임 변경 검증
    public NicknameValidationResult validateNicknameChange(String newNickname) {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(currentMemberId)
                .orElseThrow(NoMemberInfoException::new);

        boolean isSameAsCurrent = member.getNickname().equals(newNickname);
        boolean isAlreadyExists = loginMemberRepository.existsByNicknameAndDeletedFalse(newNickname);

        return new NicknameValidationResult(isSameAsCurrent, isAlreadyExists);
    }

    // 닉네임 중복 및 현재값 검증
    private void validateNewNickname(String newNickname, Long memberId) {
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(memberId).orElseThrow(NoMemberInfoException::new);

        if (member.getNickname().equals(newNickname)) {
            throw new SameNicknameException();
        }

        if(loginMemberRepository.existsByNicknameAndDeletedFalse(newNickname)) {
            throw new AlreadyExistNicknameException();
        }
    }

    // 닉네임 변경
    @Transactional
    public void changeNickname(String newNickname) {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        validateNewNickname(newNickname, currentMemberId);
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(currentMemberId)
                .orElseThrow(NoMemberInfoException::new);

        member.updateNickname(newNickname);
    }
}
