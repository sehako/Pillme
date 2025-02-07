package com.ssafy.pillme.member.infrastructure.repository;

import com.ssafy.pillme.member.domain.entity.LoginMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginMemberRepository extends JpaRepository<LoginMember, Long> {
    boolean existsByEmailAndDeletedFalse(String email);
    boolean existsByPhoneAndDeletedFalse(String phone);
    boolean existsByNicknameAndDeletedFalse(String nickname);
}
