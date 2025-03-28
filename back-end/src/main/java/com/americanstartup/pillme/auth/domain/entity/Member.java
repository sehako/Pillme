package com.americanstartup.pillme.auth.domain.entity;

import com.americanstartup.pillme.auth.domain.vo.Gender;
import com.americanstartup.pillme.auth.domain.vo.Provider;
import com.americanstartup.pillme.auth.domain.vo.Role;
import com.americanstartup.pillme.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Member extends BaseEntity {
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
    @Column(nullable = false, length = 50)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Gender gender;

    @Column(length = 30)
    private String phone;

    private boolean deleted = false;

    private boolean oauth = false;

    @Column(length = 10)
    private String birthday;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Provider provider;

    @Builder
    private Member(String email, String password, String name, String nickname,
                   Gender gender, String phone, String birthday, Provider provider, boolean deleted, boolean oauth,
                   Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.phone = phone;
        this.birthday = birthday;
        this.provider = provider;
        this.deleted = deleted;
        this.oauth = oauth;
        this.role = (role != null) ? role : Role.USER;
    }

    // 비밀번호 확인
    public boolean isPasswordMatch(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }

    // 비밀번호 재설정
    public void resetPassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
    }

    // OAuth2 사용자 추가 정보 업데이트
    public void updateAdditionalInformation(String email, String name, String nickname, Gender gender, String phone,
                                            String birthday) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.phone = phone;
        this.birthday = birthday;
    }

    // 임시 비밀번호 저장
    public void updatePassword(String temporaryPassword) {
        this.password = temporaryPassword;
    }

    // 회원 삭제
    @Override
    public void delete() {
        super.delete();
        this.deleted = true;
    }
}