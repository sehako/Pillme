package com.americanstartup.pillme.auth.application.service;

import com.americanstartup.pillme.auth.application.exception.security.InvalidMemberInfoException;
import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.auth.domain.vo.Role;
import com.americanstartup.pillme.auth.infrastructure.repository.MemberRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    // 사용자 정보를 조회하는 용도로만 사용
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member user = memberRepository.findByEmailAndDeletedFalseAndRoleNot(email, Role.LOCAL)
                .orElseThrow(InvalidMemberInfoException::new);

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()),
                user.getEmail(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}