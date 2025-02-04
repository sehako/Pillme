package com.ssafy.pillme.management.application.util;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.type.RegistrationType;

public class StatusCalculator {
    public static RegistrationType calculateStatus(
            final Information information,
            final Member member
    ) {
        RegistrationType registrationType;
        Member reader = information.getReader();
        Member writer = information.getWriter();
        if (member.getId().equals(writer.getId()) && member.getId().equals(reader.getId())) {
            registrationType = RegistrationType.MY_SELF;
        } else if (member.getId().equals(reader.getId())) {
            registrationType = RegistrationType.ADDED;
        } else {
            registrationType = RegistrationType.PUT;
        }

        return registrationType;
    }
}
