package com.ssafy.pillme.auth.presentation.request;

import org.springframework.web.bind.annotation.RequestParam;

public record VerifySmsCodeRequest(String phoneNumber, String code) {
}
