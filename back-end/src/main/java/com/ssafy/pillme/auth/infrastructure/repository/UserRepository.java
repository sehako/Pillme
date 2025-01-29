package com.ssafy.pillme.auth.infrastructure.repository;

import com.ssafy.pillme.auth.domain.entity.User;
import com.ssafy.pillme.auth.domain.vo.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndDeletedFalse(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByPhone(String phone);
    Optional<User> findByEmailAndProvider(String email, Provider provider);
}