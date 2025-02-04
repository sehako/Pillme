package com.ssafy.pillme.dependency.application.service;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.infrastructure.repository.MemberRepository;
import com.ssafy.pillme.dependency.domain.entity.Dependency;
import com.ssafy.pillme.dependency.infrastructure.repository.DependencyRepository;
import com.ssafy.pillme.dependency.presentation.request.DependencyAcceptRequest;
import com.ssafy.pillme.dependency.presentation.request.DependentPhoneRequest;
import com.ssafy.pillme.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DependencyService {
    private final DependencyRepository dependencyRepository;
    private final NotificationService notificationService;
    // TODO: MemberService로 변경
    //  피보호자 정보를 가져오는 메서드 필요
    private final MemberRepository memberRepository;

    public void requestDependency(DependentPhoneRequest request) {
        //TODO: 현재 로그인한 회원(보호자)
        Member protector = memberRepository.findById(2L).get();

        //TODO: 피보호자 정보 조회 메서드로 변경
        Member dependent = memberRepository.findById(1L).get();

//        Member dependent = memberRepository.findByPhone(request.getPhone())
//                .orElseThrow(() -> new IllegalArgumentException("피보호자 정보가 존재하지 않습니다."));

        //TODO: 이미 관계가 존재하는 경우 예외 처리 필요

        // 보호자가 피보호자에게 관계 알림 전송
        notificationService.sendDependencyRequestNotification(protector, dependent);
    }

    public void acceptDependency(DependencyAcceptRequest request) {
        //TODO: 현재 로그인한 회원(피보호자)
        Member dependent = memberRepository.findById(1L).get();

        // 보호자 정보 조회 메서드로 변경
        Member protector = memberRepository.findById(request.protectorId()).get();

        // 관계 정보 저장
        Dependency dependency = Dependency.createDependency(protector, dependent);
        dependencyRepository.save(dependency);

        // 피보호자가 보호자에게 관계 수락 알림 전송
        notificationService.sendDependencyAcceptNotification(dependent, protector);
    }
}
