package com.ssafy.pillme.dependency.application.service;

import com.ssafy.pillme.auth.application.service.AuthService;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.presentation.request.CreateLocalMemberRequest;
import com.ssafy.pillme.dependency.application.exception.DependencyNotFoundException;
import com.ssafy.pillme.dependency.application.exception.DuplicateDependencyException;
import com.ssafy.pillme.dependency.application.response.DependentListResponse;
import com.ssafy.pillme.dependency.domain.entity.Dependency;
import com.ssafy.pillme.dependency.infrastructure.repository.DependencyRepository;
import com.ssafy.pillme.dependency.presentation.request.*;
import com.ssafy.pillme.global.code.ErrorCode;
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
    private final AuthService authService;

    public void requestDependency(DependentPhoneRequest request, Member protector) {
        // 피보호자 정보 조회
        Member dependent = authService.findByPhone(request.phone());

        // 이미 관계가 존재하는 경우 예외 처리
        dependencyRepository.findByDependentIdAndProtectorIdAndDeletedIsFalse(dependent.getId(), protector.getId())
                .ifPresent(dependency -> {
                    throw new DuplicateDependencyException(ErrorCode.DUPLICATE_DEPENDENCY);
                });

        // 보호자가 피보호자에게 관계 알림 전송
        notificationService.sendDependencyRequestNotification(protector, dependent);
    }

    public void acceptDependency(DependencyAcceptRequest request, Member dependent) {
        // 보호자 정보 조회 메서드로 변경
        Member protector = authService.findById(request.protectorId());

        // 관계 정보가 존재하는지 검증
        dependencyRepository.findByDependentIdAndProtectorIdAndDeletedIsFalse(dependent.getId(), protector.getId())
                .ifPresent(dependency -> {
                    throw new DuplicateDependencyException(ErrorCode.DUPLICATE_DEPENDENCY);
                });

        // 관계 정보 저장
        Dependency dependency = Dependency.createDependency(protector, dependent);
        dependencyRepository.save(dependency);

        // 피보호자가 보호자에게 관계 수락 알림 전송
        notificationService.sendDependencyAcceptNotification(dependent, protector);
    }

    public void rejectDependency(DependencyRejectRequest request, Member dependent) {
        // 보호자 정보 조회
        Member protector = authService.findById(request.protectorId());

        // 피보호자가 보호자에게 관계 거절 알림 전송
        notificationService.sendDependencyRejectNotification(dependent, protector);
    }

    /*
     * 기존 erd 유지를 위해 회원 테이블에 이름, 성별, 생년월일을 가지는 회원 추가
     * 로그인되어 있는 회원은 보호자로, 생성된 로컬 회원은 피보호자로 관계 생성
     * */
    public void createLocalMemberWithDependency(LocalMemberRequest request, Member protector) {
        // 로컬 회원 생성
        Member dependent = authService.createLocalMember(CreateLocalMemberRequest.from(request));

        // 관계 정보 저장
        Dependency dependency = Dependency.createDependency(protector, dependent);
        dependencyRepository.save(dependency);
    }

    // 현재 로그인한 회원(보호자)의 피보호자 목록 조회
    public List<DependentListResponse> getDependents(Member protector) {

        List<Dependency> dependencies = dependencyRepository.findAllByProtectorIdAndDeletedIsFalse(protector.getId());

        return DependentListResponse.listOf(dependencies);
    }

    /*
     * 현재 로그인한 회원이 sender, receiver는 가족 관계를 맺고 있는 다른 회원
     * */
    public void deleteRequestDependency(Long dependencyId, Member loginMember) {
        // 관계 정보 조회
        Dependency dependency = dependencyRepository.findByIdAndDeletedIsFalse(dependencyId)
                .orElseThrow(() -> new DependencyNotFoundException(ErrorCode.DEPENDENCY_NOT_FOUND));

        // 가족 관계 삭제 요청 알림을 수신하는 사람
        Member receiver = dependency.getOtherMember(loginMember);

        // 가족 관계 삭제 요청 알림 전송
        notificationService.sendDependencyDeleteRequestNotification(loginMember, receiver);
    }

    // senderId를 통해 삭제 요청을 보낸 회원을 찾아서 삭제 요청을 수락
    public void acceptDeleteDependency(AcceptDependencyDeletionRequest request, Member loginMember) {
        // 현재 로그인한 회원과 senderId를 통해 삭제 요청을 보낸 회원의 관계 정보 조회
        Dependency dependency = dependencyRepository.findByMemberIdsAndDeletedIsFalse(loginMember.getId(), request.senderId())
                .orElseThrow(() -> new DependencyNotFoundException(ErrorCode.DEPENDENCY_NOT_FOUND));

        // 관계 정보 삭제
        dependency.delete();

        // 가족 관계 삭제 요청 수락 알림 전송
        notificationService.sendDependencyDeleteAcceptNotification(loginMember, dependency.getOtherMember(loginMember));
    }

    // senderId를 통해 삭제 요청을 보낸 회원을 찾아서 삭제 요청을 거절
    public void rejectDeleteDependency(RejectDependencyDeletionRequest request, Member loginMember) {
        // 현재 로그인한 회원과 senderId를 통해 삭제 요청을 보낸 회원의 관계 정보 조회
        Dependency dependency = dependencyRepository.findByMemberIdsAndDeletedIsFalse(loginMember.getId(), request.senderId())
                .orElseThrow(() -> new DependencyNotFoundException(ErrorCode.DEPENDENCY_NOT_FOUND));

        // 가족 관계 삭제 요청 거절 알림 전송
        notificationService.sendDependencyDeleteRejectNotification(loginMember, dependency.getOtherMember(loginMember));
    }

    @Transactional(readOnly = true)
    public List<Member> findProtectorsByDependent(Member dependent) {
        return dependencyRepository.findProtectorsByDependentAndDeletedIsFalse(dependent);
    }

    // 보호자가 피보호자에게 약 복용 알림을 전송
    public void sendMedicineNotification(SendMedicineNotificationRequest request, Member loginMember) {
        // 피보호자 id와 현재 로그인(보호자) 회원 id로 관계 정보 조회
        Dependency dependency = dependencyRepository.findByDependentIdAndProtectorIdAndDeletedIsFalse(request.dependentId(), loginMember.getId())
                .orElseThrow(() -> new DependencyNotFoundException(ErrorCode.DEPENDENCY_NOT_FOUND));

        // 복용 알림 전송
        notificationService.sendProtectorToDependentNotification(dependency.getProtector(), dependency.getDependent());
    }
}
