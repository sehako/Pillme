package com.ssafy.pillme.auth.infrastructure.repository;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.domain.vo.Provider;
import java.util.Optional;

import com.ssafy.pillme.auth.domain.vo.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndDeletedFalseAndRoleNot(String email, Role role);

    Optional<Member> findByPhoneAndDeletedFalseAndRoleNot(String phone, Role role);

    boolean existsByEmailAndRoleNot(String email, Role role);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

    Optional<Member> findByEmailAndProviderAndDeletedFalse(String email, Provider provider);
}