package com.americanstartup.pillme.dependency.domain.entity;

import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Entity
public class Dependency extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 보호자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protector_id", nullable = false)
    private Member protector;

    // 피보호자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependent_id", nullable = false)
    private Member dependent;

    public static Dependency createDependency(Member protector, Member dependent) {
        return Dependency.builder()
                .protector(protector)
                .dependent(dependent)
                .build();
    }

    // 현재 로그인한 회원이 아닌 다른 회원을 판별하는 메서드
    public Member getOtherMember(Member member) {
        if (member.equals(protector)) {
            return dependent;
        }
        if (member.equals(dependent)) {
            return protector;
        }

        throw new IllegalArgumentException("해당 회원은 가족 관계가 아닙니다.");
    }
}
