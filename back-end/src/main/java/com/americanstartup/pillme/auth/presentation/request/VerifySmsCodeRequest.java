package com.americanstartup.pillme.auth.presentation.request;

public record VerifySmsCodeRequest(String phoneNumber, String code) {
}
