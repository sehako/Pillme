package com.ssafy.pillme.member.application.service;

import com.ssafy.pillme.auth.application.response.MemberResponse;
import com.ssafy.pillme.global.util.SecurityUtil;
import com.ssafy.pillme.member.application.exception.*;
import com.ssafy.pillme.member.domain.entity.LoginMember;
import com.ssafy.pillme.member.domain.vo.NicknameValidationResult;
import com.ssafy.pillme.member.infrastructure.repository.LoginMemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
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
    public void changeNickname(String newNickname) {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        validateNewNickname(newNickname, currentMemberId);
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(currentMemberId)
                .orElseThrow(NoMemberInfoException::new);

        member.updateNickname(newNickname);
        loginMemberRepository.save(member);
    }
}
