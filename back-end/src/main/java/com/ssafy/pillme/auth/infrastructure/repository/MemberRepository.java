package com.ssafy.pillme.auth.infrastructure.repository;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.domain.vo.Gender;
import com.ssafy.pillme.auth.domain.vo.Provider;

import java.util.Optional;

import com.ssafy.pillme.auth.domain.vo.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndDeletedFalseAndRoleNot(String email, Role role);

    Optional<Member> findByPhoneAndDeletedFalseAndRoleNot(String phone, Role role);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByEmailAndRoleNot(String email, Role role);

    boolean existsByNicknameAndDeletedFalse(String nickname);

    boolean existsByPhoneAndDeletedFalse(String phone);

    Optional<Member> findByEmailAndProviderAndDeletedFalse(String email, Provider provider);

    Optional<Member> findByIdAndDeletedFalse(Long id);

    Optional<Member> findByPhoneAndDeletedFalse(String phone);

    boolean existsByNameAndGenderAndBirthday(String name, Gender gender, String birthday);

    Optional<Member> findByNameAndGenderAndBirthdayAndDeletedFalse(String name, Gender gender, String birthday);
}