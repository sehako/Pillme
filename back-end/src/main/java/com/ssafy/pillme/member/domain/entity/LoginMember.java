package com.ssafy.pillme.member.domain.entity;

import com.ssafy.pillme.auth.domain.vo.Gender;
import com.ssafy.pillme.auth.domain.vo.Role;
import com.ssafy.pillme.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String email;

    @Column(length = 30)
    private String name;

    @Column(length = 300)
    private String password;

    @Column(length = 30)
    private String phone;

    @Column(length = 10)
    private String birthday;

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Gender gender;

    @Column(length = 30)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Role role;

    private boolean deleted;
    private boolean oauth;

    @Builder
    private LoginMember(Long id, String email, String name, String password, String nickname,
                        Gender gender, String phone, String birthday, Role role, boolean deleted, boolean oauth) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.phone = phone;
        this.birthday = birthday;
        this.role = role;
        this.deleted = deleted;
        this.oauth = oauth;
    }

    // 회원 정보 수정
    public void updateInformation(String email, String nickname, String phone) {
        validateLocalUser(); // LOCAL 사용자 검증 추가
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
    }

    // 비밀번호 변경
    public void updatePassword(String newPassword) {
        validateLocalUser();
        this.password = newPassword;
    }

    // LOCAL 사용자 검증
    private void validateLocalUser() {
        if (Role.LOCAL.equals(this.role)) {
            throw new IllegalStateException("LOCAL 사용자는 프로필을 수정할 수 없습니다.");
        }
    }

    // 회원 탈퇴 처리
    public void delete() {
        this.deleted = true;
    }
}
