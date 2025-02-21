package com.ssafy.pillme.member.infrastructure.repository;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.member.domain.entity.LoginMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginMemberRepository extends JpaRepository<LoginMember, Long> {
    Optional<LoginMember> findByIdAndDeletedFalse(Long id);
    boolean existsByEmailAndDeletedFalse(String email);
    boolean existsByPhoneAndDeletedFalse(String phone);
    boolean existsByNicknameAndDeletedFalse(String nickname);
}
