package com.americanstartup.pillme.member.infrastructure.repository;

import com.americanstartup.pillme.member.domain.entity.LoginMember;
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
