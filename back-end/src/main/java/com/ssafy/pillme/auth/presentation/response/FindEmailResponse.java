package com.ssafy.pillme.auth.presentation.response;

import com.ssafy.pillme.auth.domain.vo.Provider;

public record FindEmailResponse(
        String email,
        Provider provider
) {}