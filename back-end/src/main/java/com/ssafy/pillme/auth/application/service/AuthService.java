package com.ssafy.pillme.auth.application.service;

import com.ssafy.pillme.auth.application.response.TokenResponse;
import com.ssafy.pillme.auth.application.response.UserResponse;
import com.ssafy.pillme.auth.domain.entity.User;
import com.ssafy.pillme.auth.domain.vo.AuthenticationInfo;
import com.ssafy.pillme.auth.domain.vo.Provider;
import com.ssafy.pillme.auth.domain.vo.UserInfo;
import com.ssafy.pillme.auth.infrastructure.repository.UserRepository;
import com.ssafy.pillme.auth.infrastructure.service.EmailService;
import com.ssafy.pillme.auth.infrastructure.service.SmsService;
import com.ssafy.pillme.auth.infrastructure.service.TokenService;
import com.ssafy.pillme.auth.presentation.request.*;
import com.ssafy.pillme.auth.presentation.response.*;
import com.ssafy.pillme.auth.util.JwtUtil;
import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final SmsService smsService;

    /**
     * 회원가입
     */
    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        // 이메일 인증 확인
        if (!emailService.isVerified(request.email())) {
            throw new CommonException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 휴대전화 인증 확인
        if (!smsService.isVerified(request.phone())) {
            throw new CommonException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        // 중복 확인
        if (userRepository.existsByEmail(request.email())) {
            throw new CommonException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(request.nickname())) {
            throw new CommonException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 회원 생성
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .nickname(request.nickname())
                .gender(request.gender())
                .phone(request.phone())
                .birthday(request.birthday())
                .oauth(false)
                .provider(Provider.FORM)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    /**
     * 로그인
     */
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedFalse(request.email())
                .orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

        if (!user.isPasswordMatch(request.password(), passwordEncoder)) {
            throw new CommonException(ErrorCode.INVALID_PASSWORD);
        }

        return createTokenResponseFromUser(user);
    }

    /**
     * OAuth2 회원가입
     */
    @Transactional
    public TokenResponse oauthSignUp(OAuthSignUpRequest request, String providerString) {
        Provider provider = Provider.valueOf(providerString.toUpperCase());

        // 이전 인증 정보 확인
        String savedProvider = tokenService.extractTempAuthInfo(request.email());
        if (savedProvider == null || !savedProvider.equals(provider.name())) {
            throw new CommonException(ErrorCode.INVALID_OAUTH_STATE);
        }

        // 휴대전화 인증 확인
        if (!smsService.isVerified(request.phone())) {
            throw new CommonException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.nickname())) {
            throw new CommonException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 회원 생성
        User user = User.builder()
                .email(request.email())
                .password(UUID.randomUUID().toString())
                .name(request.name())
                .nickname(request.nickname())
                .phone(request.phone())
                .birthday(request.birthday())
                .gender(request.gender())
                .oauth(true)
                .provider(provider)
                .build();

        return createTokenResponseFromUser(userRepository.save(user));
    }

    /**
     * OAuth2 추가 회원정보 입력 처리
     */
    @Transactional
    public UserResponse submitAdditionalInfo(Long userId, OAuthAdditionalInfoRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

        // 닉네임 중복 확인
        if (!user.isSameNickname(request.nickname()) &&
                userRepository.existsByNickname(request.nickname())) {
            throw new CommonException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 휴대전화 인증 확인
        if (!smsService.isVerified(request.phone())) {
            throw new CommonException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        // 회원 정보 업데이트
        user.updatePersonalInformation(
                request.nickname(),
                request.gender(),
                request.phone(),
                request.birthday()
        );

        return UserResponse.from(user);
    }

    /**
     * 이메일 찾기
     */
    public FindEmailResponse findEmail(String phone) {
        // 휴대전화 인증 확인
        if (!smsService.isVerified(phone)) {
            throw new CommonException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

        UserInfo userInfo = user.extractUserInfo();
        return new FindEmailResponse(userInfo.email(), userInfo.provider());
    }

    /**
     * 비밀번호 재설정 요청
     */
    @Transactional
    public void requestPasswordReset(String email, String phone) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

        if (user.isOAuthUser()) {
            throw new CommonException(ErrorCode.OAUTH_USER_PASSWORD_RESET);
        }

        if (!user.isSamePhone(phone)) {
            throw new CommonException(ErrorCode.PHONE_MISMATCH);
        }

        if (!smsService.isVerified(phone)) {
            throw new CommonException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        String resetToken = UUID.randomUUID().toString();
        tokenService.savePasswordResetToken(email, resetToken, 300000); // 5분

        emailService.sendPasswordResetEmail(email, resetToken);
    }

    /**
     * 비밀번호 재설정
     */
    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        String email = tokenService.findEmailByResetToken(request.token());
        if (email == null) {
            throw new CommonException(ErrorCode.INVALID_RESET_TOKEN);
        }

        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

        user.resetPassword(request.newPassword(), passwordEncoder);
        tokenService.deletePasswordResetToken(request.token());
    }

    /**
     * 이메일 인증번호 발송
     */
    public void sendEmailVerification(String email) {
        emailService.sendVerificationEmail(email);
    }

    /**
     * 이메일 인증번호 확인
     */
    public void verifyEmail(String email, String code) {
        emailService.verifyEmail(email, code);
    }

    /**
     * SMS 인증번호 발송
     */
    public void sendSmsVerification(String phoneNumber) {
        smsService.sendVerificationSms(phoneNumber);
    }

    /**
     * SMS 인증번호 확인
     */
    public void verifySmsCode(String phoneNumber, String code) {
        smsService.verifySmsCode(phoneNumber, code);
    }

    /**
     * 토큰 갱신
     */
    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new CommonException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        var claims = jwtUtil.extractClaims(refreshToken);
        Long userId = claims.get("userId", Long.class);
        String role = claims.get("role", String.class);

        String savedToken = tokenService.findRefreshToken(userId);
        if (!refreshToken.equals(savedToken)) {
            throw new CommonException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        return createTokenResponseWithCredentials(userId, role);
    }

    /**
     * 로그아웃
     */
    public void logout(String accessToken) {
        if (!jwtUtil.validateToken(accessToken)) {
            throw new CommonException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        var claims = jwtUtil.extractClaims(accessToken);
        Long userId = claims.get("userId", Long.class);

        tokenService.deleteRefreshToken(userId);

        long expiration = claims.getExpiration().getTime() - System.currentTimeMillis();
        tokenService.blacklistToken(accessToken, expiration);
    }

    /**
     * 사용자 정보로 토큰 응답 생성
     */
    private TokenResponse createTokenResponseFromUser(User user) {
        AuthenticationInfo authInfo = user.extractAuthenticationInfo();
        return createTokenResponseWithCredentials(authInfo.identifier(), authInfo.authority().name());
    }

    /**
     * 인증 정보로 토큰 응답 생성
     */
    private TokenResponse createTokenResponseWithCredentials(Long userId, String role) {
        String accessToken = jwtUtil.createAccessToken(userId, role);
        String refreshToken = jwtUtil.createRefreshToken(userId, role);

        tokenService.saveRefreshToken(userId, refreshToken,
                jwtUtil.extractRefreshTokenValidityPeriod());

        return TokenResponse.of(
                accessToken,
                refreshToken,
                jwtUtil.extractAccessTokenValidityPeriod(),
                jwtUtil.extractRefreshTokenValidityPeriod()
        );
    }
}