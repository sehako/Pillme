package com.ssafy.pillme.dependency.presentation.request;

import com.ssafy.pillme.auth.domain.vo.Gender;

public record LocalMemberRequest(String name, Gender gender, String birthday) {
}
