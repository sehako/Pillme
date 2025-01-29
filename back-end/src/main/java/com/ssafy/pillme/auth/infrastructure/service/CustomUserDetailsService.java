package com.ssafy.pillme.auth.infrastructure.service;

import com.ssafy.pillme.auth.domain.entity.User;
import com.ssafy.pillme.auth.infrastructure.repository.UserRepository;
import com.ssafy.pillme.global.exception.CommonException;
import com.ssafy.pillme.global.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

        return new org.springframework.security.core.userdetails.User(
                user.extractAuthenticationInfo().identifier().toString(),
                user.extractUserInfo().email(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.extractAuthenticationInfo().authority()))
        );
    }
}