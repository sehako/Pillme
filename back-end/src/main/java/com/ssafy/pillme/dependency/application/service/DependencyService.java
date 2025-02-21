package com.ssafy.pillme.dependency.application.service;

import com.ssafy.pillme.auth.application.service.AuthService;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.domain.vo.Role;
import com.ssafy.pillme.auth.presentation.request.CreateLocalMemberRequest;
import com.ssafy.pillme.chat.application.service.ChatRoomService;
import com.ssafy.pillme.dependency.application.exception.DependencyNotFoundException;
import com.ssafy.pillme.dependency.application.exception.DuplicateDependencyException;
import com.ssafy.pillme.dependency.application.exception.SelfDependencyRequestException;
import com.ssafy.pillme.dependency.application.response.DependentListResponse;
import com.ssafy.pillme.dependency.application.response.RelationShipListResponse;
import com.ssafy.pillme.dependency.domain.entity.Dependency;
import com.ssafy.pillme.dependency.infrastructure.repository.DependencyRepository;
import com.ssafy.pillme.dependency.presentation.request.*;
import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DependencyService {
    private final RedisTemplate<String, String> redisTemplate;
    private final DependencyRepository dependencyRepository;
    private final NotificationService notificationService;
    private final AuthService authService;
    private final ChatRoomService chatRoomService;

    private static final String DEPENDENCY_DELETE_REQUEST_KEY = "dependency:delete:request:";

    public void requestDependency(DependentPhoneRequest request, Member protector) {
        // 피보호자 정보 조회
        Member dependent = authService.findByPhone(request.phone());

        // 자기 자신에게 관계 요청하는 경우 예외 처리
        if (protector.getId().equals(dependent.getId())) {
            throw new SelfDependencyRequestException(ErrorCode.SELF_DEPENDENCY_REQUEST);
        }

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

        // 채팅방 생성
        chatRoomService.createChatRoom(dependent, protector);

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
        /*
         * 이름, 성별, 생년월일이 동일한 회원이 존재하면 해당 회원과 보호자의 관계가 존재하는지 확인
         *   관계가 존재하면 예외 처리
         *   관계가 존재하지 않으면 관계 정보 저장
         * 존재하지 않으면 로컬 회원 생성 후 관계 정보 저장
         * */
//        Member dependent = authService.findLocalMember(request.name(), request.gender(), request.birthday());
//
//        if (dependent != null) {
//            dependencyRepository.findByDependentIdAndProtectorIdAndDeletedIsFalse(dependent.getId(), protector.getId())
//                    .ifPresent(dependency -> {
//                        throw new DuplicateDependencyException(ErrorCode.DUPLICATE_DEPENDENCY);
//                    });
//        } else {
//            // 로컬 회원 생성
//            dependent = authService.createLocalMember(CreateLocalMemberRequest.from(request));
//        }

        // 로컬 회원은 중복을 허용하고 관계 정보 저장
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

        /* 로컬 회원으로 등록된 피보호자인 경우
         * 알림 없이 관계 정보 삭제 및 로컬 회원 삭제
         * */
        if (dependency.getDependent().getRole().equals(Role.LOCAL)) {
            dependency.delete();
            authService.deleteLocalMember(dependency.getDependent());
            return;
        }

        // 가족 관계 삭제 요청 알림을 수신하는 사람
        Member receiver = dependency.getOtherMember(loginMember);

        // 가족 관계 삭제 요청 알림 전송
        notificationService.sendDependencyDeleteRequestNotification(loginMember, receiver, dependencyId);
    }

    // senderId를 통해 삭제 요청을 보낸 회원을 찾아서 삭제 요청을 수락
    public void acceptDeleteDependency(AcceptDependencyDeletionRequest request, Member loginMember) {
        long dependencyId;

        // 서비스 내부 알림 리스트에서 삭제 요청을 수락할 때 사용
        if (request.dependencyId() == null) {
            String dependencyIdStr = redisTemplate.opsForValue().get(DEPENDENCY_DELETE_REQUEST_KEY + request.notificationId());
            if (dependencyIdStr == null) {
                throw new DependencyNotFoundException(ErrorCode.DEPENDENCY_NOT_FOUND);
            }
            dependencyId = Long.parseLong(dependencyIdStr);
        } else {
            dependencyId = request.dependencyId();
        }

        // 요청에 존재하는 dependencyId로 관계 정보 조회
        Dependency dependency = dependencyRepository.findByIdAndDeletedIsFalse(dependencyId)
                .orElseThrow(() -> new DependencyNotFoundException(ErrorCode.DEPENDENCY_NOT_FOUND));

        // 관계 정보가 올바른지 검증
        checkValidatedDependency(dependency, loginMember, request.senderId());

        /* 두 회원이 서로 보호자, 피보호자 관계를 가지는 경우 채팅방 삭제 X
         * 관계가 1:1 관계인 경우만 채팅방 삭제
         * */
        List<Dependency> dependencies = dependencyRepository.findByMemberIdsAndDeletedIsFalse(dependency.getProtector().getId(), dependency.getDependent().getId());
        if (dependencies.size() == 1) {
            // 채팅방 삭제
            chatRoomService.deleteChatRoom(dependency.getProtector(), dependency.getDependent());
        }

        // 관계 정보 삭제
        dependency.delete();

        // redis 키 삭제
        redisTemplate.delete(DEPENDENCY_DELETE_REQUEST_KEY + request.notificationId());

        // 가족 관계 삭제 요청 수락 알림 전송
        notificationService.sendDependencyDeleteAcceptNotification(loginMember, dependency.getOtherMember(loginMember));
    }

    // senderId를 통해 삭제 요청을 보낸 회원을 찾아서 삭제 요청을 거절
    public void rejectDeleteDependency(RejectDependencyDeletionRequest request, Member loginMember) {
        long dependencyId;

        // 서비스 내부 알림 리스트에서 삭제 요청을 거절할 때 사용
        if (request.dependencyId() == null) {
            String dependencyIdStr = redisTemplate.opsForValue().get(DEPENDENCY_DELETE_REQUEST_KEY + request.notificationId());
            if (dependencyIdStr == null) {
                throw new DependencyNotFoundException(ErrorCode.DEPENDENCY_NOT_FOUND);
            }
            dependencyId = Long.parseLong(dependencyIdStr);
        } else {
            dependencyId = request.dependencyId();
        }

        // 요청에 존재하는 dependencyId로 관계 정보 조회
        Dependency dependency = dependencyRepository.findByIdAndDeletedIsFalse(dependencyId)
                .orElseThrow(() -> new DependencyNotFoundException(ErrorCode.DEPENDENCY_NOT_FOUND));

        // 관계 정보가 올바른지 검증
        checkValidatedDependency(dependency, loginMember, request.senderId());

        // redis 키 삭제
        redisTemplate.delete(DEPENDENCY_DELETE_REQUEST_KEY + request.notificationId());

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

    @Transactional(readOnly = true)
    public RelationShipListResponse getRelationships(Member loginMember) {
        // 로그인되어있는 회원의 모든 관계 정보 조회
        List<Dependency> dependencies = dependencyRepository.findAllByProtectorIdAndDeletedIsFalseOrDependentIdAndDeletedIsFalse(loginMember.getId());

        return RelationShipListResponse.of(dependencies, loginMember.getId());
    }

    // 보호자와 피보호자 간의 관계가 존재하는지 확인
    @Transactional(readOnly = true)
    public boolean isDependencyExist(Member protector, Member dependent) {
        return dependencyRepository.findByDependentIdAndProtectorIdAndDeletedIsFalse(dependent.getId(), protector.getId()).isPresent();
    }

    // 현재 로그인한 회원과 요청으로 보낸 회원이 관계 정보를 가지고 있는지 확인
    private void checkValidatedDependency(Dependency dependency, Member loginMember, Long senderId) {
        if (!dependency.getProtector().getId().equals(loginMember.getId()) && !dependency.getDependent().getId().equals(loginMember.getId())) {
            throw new DependencyNotFoundException(ErrorCode.DEPENDENCY_NOT_FOUND);
        }

        if (!dependency.getProtector().getId().equals(senderId) && !dependency.getDependent().getId().equals(senderId)) {
            throw new DependencyNotFoundException(ErrorCode.DEPENDENCY_NOT_FOUND);
        }
    }
}
