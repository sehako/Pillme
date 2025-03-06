package com.americanstartup.pillme.auth.application.response;

import com.americanstartup.pillme.auth.domain.vo.Provider;

public record FindEmailResponse(
        String email,
        Provider provider
) {}