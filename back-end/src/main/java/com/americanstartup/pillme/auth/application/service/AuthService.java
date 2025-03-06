package com.americanstartup.pillme.auth.application.service;

import com.americanstartup.pillme.auth.application.exception.security.*;
import com.americanstartup.pillme.auth.application.exception.token.DenylistedTokenException;
import com.americanstartup.pillme.auth.application.exception.token.InvalidAccessTokenException;
import com.americanstartup.pillme.auth.application.exception.token.InvalidRefreshTokenException;
import com.americanstartup.pillme.auth.application.exception.validation.DuplicateEmailAddressException;
import com.americanstartup.pillme.auth.application.exception.validation.DuplicateMemberNicknameException;
import com.americanstartup.pillme.auth.application.exception.validation.DuplicatePhoneNumberException;
import com.americanstartup.pillme.auth.application.response.FindEmailResponse;
import com.americanstartup.pillme.auth.application.response.MemberResponse;
import com.americanstartup.pillme.auth.application.response.TokenResponse;
import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.auth.domain.vo.Gender;
import com.americanstartup.pillme.auth.domain.vo.Provider;
import com.americanstartup.pillme.auth.domain.vo.Role;
import com.americanstartup.pillme.auth.infrastructure.repository.MemberRepository;
import com.americanstartup.pillme.auth.presentation.request.CreateLocalMemberRequest;
import com.americanstartup.pillme.auth.presentation.request.LoginRequest;
import com.americanstartup.pillme.auth.presentation.request.SignUpRequest;
import com.americanstartup.pillme.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        if (!smsService.isVerified(request.phone())) {
            throw new UnverifiedPhoneNumberException();
        }

        // 중복 확인
        if (memberRepository.existsByEmailAndRoleNotAndDeletedFalse(request.email(), Role.LOCAL)) {
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
     * 임시 비밀번호 발급을 위한 이메일 검증
     */
    public void verifyEmailForPasswordReset(String email) {
        // 현재 존재하는 회원의 이메일인지 검증
        if (!memberRepository.existsByEmailAndDeletedFalse(email)) {
            throw new InvalidMemberInfoException();
        }

        // 이메일 인증번호 발송
        emailService.sendVerificationEmail(email);
    }

    /**
     * 임시 비밀번호 생성 요청
     */
    @Transactional
    public void sendTemporaryPasswordEmail(String email, String phone) {
        // 이메일 인증 확인
        if (!emailService.isVerified(email)) {
            throw new UnverifiedEmailAddressException();
        }

        // 이메일과 전화번호로 회원 검증
        Member member = memberRepository.findByEmailAndPhoneAndDeletedFalse(email, phone)
                .orElseThrow(InvalidMemberInfoException::new);

        String temporaryPassword;
        do {
            temporaryPassword = generateTemporaryPassword();
        } while (!validatePassword(temporaryPassword));  // 생성된 비밀번호가 정규식 패턴에 맞는지 확인

        member.updatePassword(passwordEncoder.encode(temporaryPassword));

        emailService.sendTemporaryPassword(email, temporaryPassword);
    }

    /**
     * 임시 비밀번호 생성
     * 규칙: 12자리, 대문자, 소문자, 숫자, 특수문자 각각 1개 이상 포함
     */
    private String generateTemporaryPassword() {
        String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerChars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";  // 정규식 패턴에 맞는 특수문자 목록으로 수정

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        // 각 필수 문자 타입에서 하나씩 선택 (4자리)
        password.append(upperChars.charAt(random.nextInt(upperChars.length())));
        password.append(lowerChars.charAt(random.nextInt(lowerChars.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // 나머지 8자리를 모든 문자 타입에서 랜덤하게 선택
        String allChars = upperChars + lowerChars + numbers + specialChars;
        for (int i = 0; i < 8; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 문자열을 섞어서 패턴이 예측되지 않도록 함
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
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
        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[~`!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?])[A-Za-z\\d~`!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?]{12,300}$";

        try {
            return Pattern.matches(pattern, password);
        } catch (Exception e) {
            throw new InvalidPasswordFormatException();
        }
    }

    /**
     * 닉네임 중복 검사
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
