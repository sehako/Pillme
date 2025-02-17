package com.ssafy.pillme.admin.application.service;

import com.ssafy.pillme.admin.Infrastructure.repository.SigninMemberRepository;
import com.ssafy.pillme.admin.application.response.SinginMemberResponse;
import com.ssafy.pillme.admin.domain.entity.SigninMember;
import com.ssafy.pillme.admin.presentation.request.MemberUpdateRequest;
import com.ssafy.pillme.auth.domain.vo.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final SigninMemberRepository signinMemberRepository;  // static 제거, final 추가

    // 전체 회원, 활성 회원, 탈퇴 회원 수 조회
    public long countTotalUser() {
        return signinMemberRepository.countByRole(Role.USER);
    }

    public long countActiveUser() {
        return signinMemberRepository.countByRoleAndDeletedFalse(Role.USER);
    }

    public long countDeletedUser() {
        return signinMemberRepository.countByRoleAndDeleted(Role.USER, true);
    }

    // 멤버 목록 페이지 조회
    public Page<SinginMemberResponse> listUpUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return signinMemberRepository.findByRole(Role.USER, pageable);
    }

    // 회원 탈퇴 처리
    @Transactional
    public void updateMemberStatus(Long memberId, boolean isDeleted) {
        SigninMember member = signinMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        member.delete(isDeleted);
    }

    // 회원 정보 수정
    @Transactional
    public void updateMemberInfo(Long memberId, MemberUpdateRequest request) {
        SigninMember member = signinMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        member.updateInfo(request);
    }

    // 검색 결과
    public Page<SinginMemberResponse> searchMembers(String keyword, Pageable pageable) {
        return signinMemberRepository.findByNameContainingOrEmailContaining(
                keyword, keyword, pageable);
    }


}
