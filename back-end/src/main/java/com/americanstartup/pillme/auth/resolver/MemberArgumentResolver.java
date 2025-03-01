package com.americanstartup.pillme.auth.resolver;

import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.auth.infrastructure.repository.MemberRepository;
import com.americanstartup.pillme.auth.annotation.Auth;
import com.americanstartup.pillme.auth.application.exception.security.InvalidMemberInfoException;
import com.americanstartup.pillme.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Auth.class) != null
                && parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Long memberId = SecurityUtil.extractCurrentMemberId();
        return memberRepository.findById(memberId)
                .orElseThrow(InvalidMemberInfoException::new);
    }
}