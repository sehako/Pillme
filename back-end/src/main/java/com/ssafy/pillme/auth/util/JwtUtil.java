package com.ssafy.pillme.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public JwtUtil(
            @Value("${JWT_SECRET}") String jwtSecret,
            @Value("${JWT_ACCESS_VALIDITY}") long accessTokenValidityTime,
            @Value("${JWT_REFRESH_VALIDITY}") long refreshTokenValidityTime
    ) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.accessTokenValidityTime = accessTokenValidityTime;
        this.refreshTokenValidityTime = refreshTokenValidityTime;
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(Long memberId, String role) {
        return createToken(memberId, role, accessTokenValidityTime);
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Long memberId, String role) {
        return createToken(memberId, role, refreshTokenValidityTime);
    }

    /**
     * Access Token 유효 시간 추출
     */
    public long extractAccessTokenValidityPeriod() {
        return this.accessTokenValidityTime;
    }

    /**
     * Refresh Token 유효 시간 추출
     */
    public long extractRefreshTokenValidityPeriod() {
        return this.refreshTokenValidityTime;
    }

    /**
     * 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 클레임 추출
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰에서 memberId 추출
     */
    public Long getUserIdFromToken(String token) {
        if (!validateToken(token)) {
            return null;
        }

        try {
            Claims claims = extractClaims(token);
            return claims.get("memberId", Long.class);
        } catch (Exception e) {
            log.error("Failed to extract userId from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 토큰 생성
     */
    private String createToken(Long memberId, String role, long validityTime) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        claims.put("role", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}