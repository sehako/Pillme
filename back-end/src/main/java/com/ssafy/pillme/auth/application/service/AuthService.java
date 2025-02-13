package com.ssafy.pillme.auth.application.service;

import com.ssafy.pillme.auth.application.exception.oauth.*;
import com.ssafy.pillme.auth.application.exception.security.*;
import com.ssafy.pillme.auth.application.exception.validation.*;
import com.ssafy.pillme.auth.application.exception.token.*;
import com.ssafy.pillme.auth.application.response.TokenResponse;
import com.ssafy.pillme.auth.application.response.MemberResponse;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.domain.vo.Gender;
import com.ssafy.pillme.auth.domain.vo.Provider;
import com.ssafy.pillme.auth.domain.vo.Role;
import com.ssafy.pillme.auth.infrastructure.repository.MemberRepository;
import com.ssafy.pillme.auth.presentation.request.*;
import com.ssafy.pillme.auth.application.response.FindEmailResponse;
import com.ssafy.pillme.auth.util.JwtUtil;

import java.util.UUID;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final SmsService smsService;

    /**
     * 회원가입
     */
    @Transactional
    public MemberResponse signUp(SignUpRequest request) {
        // 이메일 인증 확인
        if (!emailService.isVerified(request.email())) {
            throw new UnverifiedEmailAddressException();
        }

        // 휴대전화 인증 확인
//        if (!smsService.isVerified(request.phone())) {
//            throw new UnverifiedPhoneNumberException();
//        }

        // 중복 확인
        if (memberRepository.existsByEmailAndRoleNot(request.email(), Role.LOCAL)) {
            throw new DuplicateEmailAddressException();
        }
        if (memberRepository.existsByNicknameAndDeletedFalse(request.nickname())) {
            throw new DuplicateMemberNicknameException();
        }
        if (memberRepository.existsByPhoneAndDeletedFalse(request.phone())) {
            throw new DuplicatePhoneNumberException();
        }

        // 회원 생성
        Member member = Member.builder()
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

        return MemberResponse.from(memberRepository.save(member));
    }

    /**
     * 로그인
     */
    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmailAndDeletedFalseAndRoleNot(request.email(), Role.LOCAL)
                .orElseThrow(InvalidMemberInfoException::new);

        if (!member.isPasswordMatch(request.password(), passwordEncoder)) {
            throw new InvalidMemberPasswordException();
        }

        return createTokenResponseFromMember(member);
    }

    /**
     * 이메일 찾기
     */
    public FindEmailResponse findEmail(String phone) {
        // 휴대전화 인증 확인
        if (!smsService.isVerified(phone)) {
            throw new UnverifiedPhoneNumberException();
        }

        Member member = memberRepository.findByPhoneAndDeletedFalseAndRoleNot(phone, Role.LOCAL)
                .orElseThrow(InvalidMemberInfoException::new);
        return new FindEmailResponse(member.getEmail(), member.getProvider());
    }

    /**
     * 비밀번호 재설정 요청
     */
    @Transactional
    public void requestPasswordReset(String email, String phone) {
        Member member = memberRepository.findByEmailAndDeletedFalseAndRoleNot(email, Role.LOCAL)
                .orElseThrow(InvalidMemberInfoException::new);

        if (member.isOauth()) {
            throw new RestrictedOAuthPasswordException();
        }

        if (!phone.equals(member.getPhone())) {
            throw new MismatchedPhoneNumberException();
        }

        if (!smsService.isVerified(phone)) {
            throw new UnverifiedPhoneNumberException();
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
            throw new InvalidResetTokenException();
        }

        Member member = memberRepository.findByEmailAndDeletedFalseAndRoleNot(email, Role.LOCAL)
                .orElseThrow(InvalidMemberInfoException::new);

        member.resetPassword(request.newPassword(), passwordEncoder);
        tokenService.deletePasswordResetToken(request.token());
    }

    /**
     * 토큰 갱신
     */
    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        var claims = jwtUtil.extractClaims(refreshToken);
        Long memberId = claims.get("memberId", Long.class);
        String role = claims.get("role", String.class);

        String savedToken = tokenService.findRefreshToken(memberId);
        if (!refreshToken.equals(savedToken)) {
            throw new InvalidRefreshTokenException();
        }

        return createTokenResponseWithCredentials(memberId, role);
    }

    /**
     * 로그아웃
     */
    public void logout(String accessToken) {
        if (tokenService.isTokenDenylisted(accessToken)) {
            throw new DenylistedTokenException();
        }

        if (!jwtUtil.validateToken(accessToken)) {
            throw new InvalidAccessTokenException();
        }

        var claims = jwtUtil.extractClaims(accessToken);
        Long memberId = claims.get("memberId", Long.class);

        tokenService.deleteRefreshToken(memberId);

        // Access Token을 거부 목록에 추가
        // 남은 만료 시간만큼만 거부 목록에 보관
        long expiration = claims.getExpiration().getTime() - System.currentTimeMillis();
        tokenService.denylistToken(accessToken, expiration);
    }

    /**
     * 사용자 정보로 토큰 응답 생성
     */
    public TokenResponse createTokenResponseFromMember(Member member) {
        return createTokenResponseWithCredentials(member.getId(), member.getRole().name());
    }

    /**
     * 인증 정보로 토큰 응답 생성
     */
    private TokenResponse createTokenResponseWithCredentials(Long memberId, String role) {
        String accessToken = jwtUtil.createAccessToken(memberId, role);
        String refreshToken = jwtUtil.createRefreshToken(memberId, role);

        tokenService.saveRefreshToken(memberId, refreshToken,
                jwtUtil.extractRefreshTokenValidityPeriod());

        return TokenResponse.of(
                accessToken,
                refreshToken,
                jwtUtil.extractAccessTokenValidityPeriod(),
                jwtUtil.extractRefreshTokenValidityPeriod()
        );
    }

    /**
     * 비밀번호 유효성 검증
     */
    public boolean validatePassword(String password) {
        // 비밀번호 정규식 패턴
        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[~`!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?])[A-Za-z\\d~`!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?]{12}$";

        try {
            return Pattern.matches(pattern, password);
        } catch (Exception e) {
            throw new InvalidPasswordFormatException();
        }
    }

    /**
     * 닉네임 중복 검사
     *
     * @return true: 중복, false: 사용 가능
     */
    public boolean checkNicknameDuplicate(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new DuplicateMemberNicknameException();
        }

        return memberRepository.existsByNicknameAndDeletedFalse(nickname);
    }

    /**
     * 회원 정보 조회
     */
    public Member findById(Long id) {
        return memberRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(InvalidMemberInfoException::new);
    }

    /**
     * 로컬 회원 생성
     */
    public Member createLocalMember(CreateLocalMemberRequest request) {
        // 이메일 생성 (50자 제한, unique 보장)
        String email = "local_" + UUID.randomUUID().toString().substring(0, 20) + "@example.com";

        // 전화번호 생성 (30자 제한)
        String phone = "999" + UUID.randomUUID().toString().substring(0, 7);

        // 비밀번호 생성 (300자 제한)
        String password = UUID.randomUUID().toString();

        Member localMember = Member.builder()
                .name(request.name())
                .gender(request.gender())
                .birthday(request.birthday())
                .email(email)
                .password(password)
                .nickname(request.name())
                .phone(phone)
                .deleted(false)
                .oauth(false)
                .role(Role.LOCAL)
                .build();
        return memberRepository.save(localMember);
    }

    /**
     * 휴대번호로 회원 조회
     */
    public Member findByPhone(String phone) {
        return memberRepository.findByPhoneAndDeletedFalse(phone)
                .orElseThrow(InvalidMemberInfoException::new);
    }

    /**
     * 이름, 성별, 생년월일로 로컬 회원 조회
     */
    public Member findLocalMember(String name, Gender gender, String birthday) {
        return memberRepository.findByNameAndGenderAndBirthdayAndDeletedFalse(name, gender, birthday)
                .orElse(null);
    }

    /**
     * 로컬 회원 삭제
     */
    @Transactional
    public void deleteLocalMember(Member member) {
        member.delete();
    }
}
