package com.ssafy.pillme.auth.domain.vo;

/**
 * 인증 정보를 담는 클래스
 */
public record AuthenticationInfo(
        Long id,
        Role role
) {
    public static AuthenticationInfo of(Long id, Role role) {
        return new AuthenticationInfo(id, role);
    }

    /**
     * 사용자 ID 조회
     */
    public Long getUserId() {
        return this.id;
    }

    /**
     * 사용자 권한 조회
     */
    public Role getRole() {
        return this.role;
    }
}