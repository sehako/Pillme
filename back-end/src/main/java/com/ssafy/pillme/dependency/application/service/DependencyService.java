package com.ssafy.pillme.dependency.application.service;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.infrastructure.repository.MemberRepository;
import com.ssafy.pillme.dependency.application.response.DependentListResponse;
import com.ssafy.pillme.dependency.domain.entity.Dependency;
import com.ssafy.pillme.dependency.infrastructure.repository.DependencyRepository;
import com.ssafy.pillme.dependency.presentation.request.DependencyAcceptRequest;
import com.ssafy.pillme.dependency.presentation.request.DependencyRejectRequest;
import com.ssafy.pillme.dependency.presentation.request.DependentPhoneRequest;
import com.ssafy.pillme.dependency.presentation.request.LocalMemberRequest;
import com.ssafy.pillme.member.application.service.MemberService;
import com.ssafy.pillme.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        //TODO: 관계 등록 요청이 존재하는지 검증 필요

        // 관계 정보 저장
        Dependency dependency = Dependency.createDependency(protector, dependent);
        dependencyRepository.save(dependency);

        // 피보호자가 보호자에게 관계 수락 알림 전송
        notificationService.sendDependencyAcceptNotification(dependent, protector);
    }

    public void rejectDependency(DependencyRejectRequest request) {
        //TODO: 현재 로그인한 회원(피보호자)
        Member dependent = memberRepository.findById(1L).get();

        //TODO: 보호자 정보 조회 메서드로 변경
        Member protector = memberRepository.findById(request.protectorId()).get();

        //TODO: 관계 등록 요청이 존재하는지 검증 필요

        // 피보호자가 보호자에게 관계 거절 알림 전송
        notificationService.sendDependencyRejectNotification(dependent, protector);
    }

    /*
     * 기존 erd 유지를 위해 회원 테이블에 이름, 성별, 생년월일을 가지는 회원 추가
     * 로그인되어 있는 회원은 보호자로, 생성된 로컬 회원은 피보호자로 관계 생성
     * */
    public void createLocalMemberWithDependency(LocalMemberRequest request) {
        //TODO: 현재 로그인한 회원(보호자)
        Member protector = memberRepository.findById(2L).get();

        //TODO: 이미 관계가 존재하는 경우 예외 처리 필요

        //TODO: memberService에서 회원 생성 메서드 추가
        // 로컬 회원 생성
        Member dependent = Member.builder()
                .name(request.name())
                .gender(request.gender())
                .birthday(request.birthday())
                .build();

        // 관계 정보 저장
        Dependency dependency = Dependency.createDependency(protector, dependent);
        dependencyRepository.save(dependency);
    }

    public List<DependentListResponse> getDependents() {
        // TODO: 현재 로그인한 회원(보호자)
        Member protector = memberRepository.findById(3L).get();

        // 보호자의 피보호자 목록 조회
        List<Dependency> dependencies = dependencyRepository.findAllByProtectorIdAndDeletedIsFalse(protector.getId());

        return DependentListResponse.listOf(dependencies);
    }
}
