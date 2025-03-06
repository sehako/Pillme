package com.americanstartup.pillme.dependency.presentation.request;

import com.americanstartup.pillme.auth.domain.vo.Gender;

public record LocalMemberRequest(String name, Gender gender, String birthday) {
}
