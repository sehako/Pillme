package com.ssafy.pillme.admin.domain.entity;

import com.ssafy.pillme.admin.presentation.request.MemberUpdateRequest;
import com.ssafy.pillme.auth.domain.vo.Gender;
import com.ssafy.pillme.auth.domain.vo.Provider;
import com.ssafy.pillme.auth.domain.vo.Role;
import com.ssafy.pillme.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
public class SigninMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String name;

    @Column(length = 50)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 30)
    private String nickname;

    @Column(length = 10)
    private String birthday;

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Gender gender;

    private boolean oauth = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Provider provider;

    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Role role;

    @Builder
    private SigninMember(String name, String email, String phone, String nickname, String birthday, Gender gender, boolean oauth, Provider provider, boolean deleted, Role role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.oauth = oauth;
        this.provider = provider;
        this.deleted = deleted;
        this.role = role;
    }

    public void updateInfo(MemberUpdateRequest request) {
        this.name = request.name();
        this.email = request.email();
        this.phone = request.phone();
        this.nickname = request.nickname();
        this.birthday = request.birthday();
        this.gender = request.gender();
    }

    // 회원 삭제
    public void delete(Boolean isDeleted) {
        this.deleted = isDeleted;
    }
}