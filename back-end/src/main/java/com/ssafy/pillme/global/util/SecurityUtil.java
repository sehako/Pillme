package com.ssafy.pillme.global.util;

import com.ssafy.pillme.auth.application.exception.security.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    /**
     * 현재 인증된 사용자의 ID 추출
     */
    public static Long extractCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new SecurityContextAuthInfoNotFoundException();
        }

        Object principal = authentication.getPrincipal();
        if (principal == null) {
            throw new SecurityContextAuthInfoNotFoundException();
        }

        try {
            return Long.parseLong(principal.toString());
        } catch (NumberFormatException e) {
            throw new InvalidMemberIdFormatException();
        }
    }

    /**
     * 현재 인증된 사용자의 권한 추출
     */
    public static String extractCurrentMemberRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities() == null) {
            throw new SecurityContextRoleInfoNotFoundException();
        }

        return authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(SecurityContextRoleInfoNotFoundException::new)
                .getAuthority();
    }

    /**
     * 현재 사용자가 인증되었는지 확인
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }
}

