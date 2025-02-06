package com.ssafy.pillme.dependency.presentation.request;

// 확장성을 위해 DependencyAcceptRequest, DependencyRejectRequest를 분리
public record DependencyRejectRequest(Long protectorId) {
}
