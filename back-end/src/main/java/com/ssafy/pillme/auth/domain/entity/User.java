package com.ssafy.pillme.auth.domain.entity;

import com.ssafy.pillme.auth.domain.vo.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 300)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(unique = true, nullable = false, length = 30)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Gender gender;

    @Column(length = 30)
    private String phone;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private boolean deleted = false;

    private boolean oauth = false;

    @Column(length = 10)
    private String birthday;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Provider provider;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    @Builder
    private User(String email, String password, String name, String nickname,
                 Gender gender, String phone, String birthday, Provider provider, boolean oauth) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.phone = phone;
        this.birthday = birthday;
        this.provider = provider;
        this.oauth = oauth;
    }

    // 비밀번호 확인
    public boolean isPasswordMatch(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }

    // 사용자 롤 확인
    public boolean hasRole(Role roleToCheck) {
        return this.role == roleToCheck;
    }

    // 사용자 정보 UserInfo 객체로 추출
    public UserInfo extractUserInfo() {
        return new UserInfo(
                this.id,
                this.email,
                this.name,
                this.nickname,
                this.role,
                this.gender,
                this.phone,
                this.birthday,
                this.oauth,
                this.provider
        );
    }

    // 비밀번호 재설정
    public void resetPassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
        this.modifiedAt = LocalDateTime.now();
    }

    // 회원 탈퇴
    public void delete() {
        this.deleted = true;
        this.modifiedAt = LocalDateTime.now();
    }

    // 탈퇴한 회원 판별
    public boolean isDeleted() {
        return this.deleted;
    }

    // 오어스 유저 판별
    public boolean isOAuthUser() {
        return this.oauth;
    }

    // 인증 정보
    public AuthenticationInfo extractAuthenticationInfo() {
        return new AuthenticationInfo(this.id, this.role);
    }

    // 이메일 중복 확인
    public boolean isSameEmail(String emailToCheck) {
        return this.email.equals(emailToCheck);
    }

    // 휴대전화 중복 확인
    public boolean isSamePhone(String phoneToCheck) {
        return this.phone != null && this.phone.equals(phoneToCheck);
    }

    // 닉네임 중복 확인
    public boolean isSameNickname(String nicknameToCheck) {
        return this.nickname.equals(nicknameToCheck);
    }
}