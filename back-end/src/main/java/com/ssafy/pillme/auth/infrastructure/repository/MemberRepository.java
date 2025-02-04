package com.ssafy.pillme.auth.infrastructure.repository;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.domain.vo.Provider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndDeletedFalse(String email);

    Optional<Member> findByPhoneAndDeletedFalse(String phone);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

    Optional<Member> findByEmailAndProviderAndDeletedFalse(String email, Provider provider);
}