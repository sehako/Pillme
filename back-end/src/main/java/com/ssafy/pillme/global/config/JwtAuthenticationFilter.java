package com.ssafy.pillme.global.config;

import com.ssafy.pillme.auth.infrastructure.service.TokenService;
import com.ssafy.pillme.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Authorization 헤더에서 JWT 토큰 추출
        String token = extractToken(request);

        // 토큰이 유효하고 블랙리스트에 없는 경우에만 인증 처리
        if (token != null && jwtUtil.validateToken(token) && !tokenService.isTokenBlacklisted(token)) {
            var claims = jwtUtil.extractClaims(token);
            // SecurityContext에 인증 정보 설정
            var authentication = new UsernamePasswordAuthenticationToken(
                    claims.get("userId"),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + claims.get("role")))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}