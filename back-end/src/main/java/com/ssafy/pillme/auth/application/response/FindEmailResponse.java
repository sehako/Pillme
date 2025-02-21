package com.ssafy.pillme.auth.application.response;

import com.ssafy.pillme.auth.domain.vo.Provider;

public record FindEmailResponse(
        String email,
        Provider provider
) {}