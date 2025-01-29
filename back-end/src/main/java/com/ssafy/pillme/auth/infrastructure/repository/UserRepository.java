package com.ssafy.pillme.auth.infrastructure.repository;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.domain.vo.Provider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailAndDeletedFalse(String email);

    Optional<Member> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

    Optional<Member> findByEmailAndProvider(String email, Provider provider);
}