package com.ssafy.pillme.admin.Infrastructure.repository;

import com.ssafy.pillme.admin.application.response.SinginMemberResponse;
import com.ssafy.pillme.admin.domain.entity.SigninMember;
import com.ssafy.pillme.auth.domain.vo.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SigninMemberRepository extends JpaRepository<SigninMember, Long> {
    @Override
    Optional<SigninMember> findById(Long id);

    // 특정 롤을 가진 회원 수
    long countByRole(Role role);

    // 특정 롤을 가진 활성 회원 수
    long countByRoleAndDeletedFalse(Role role);

    // 특정 롤을 가진 탈퇴 회원 수
    long countByRoleAndDeleted(Role role, boolean deleted);

    // 특정 role의 페이징 처리된 회원 리스트
    Page<SinginMemberResponse> findByRole(Role role, Pageable pageable);

    // 검색 결과 페이징 처리된 리스트
    Page<SinginMemberResponse> findByNameContainingOrEmailContaining(
            String name, String email, Pageable pageable);
}
