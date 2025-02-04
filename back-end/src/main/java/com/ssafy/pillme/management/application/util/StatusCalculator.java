package com.ssafy.pillme.management.application.util;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.management.domain.Information;

public class StatusCalculator {
    public static int calculateStatus(
            final Information information,
            final Member member
    ) {
        int status;
        Member reader = information.getReader();
        Member writer = information.getWriter();
        if (member.getId().equals(writer.getId()) && member.getId().equals(reader.getId())) {
            status = 1;
        } else if (member.getId().equals(reader.getId())) {
            status = 2;
        } else {
            status = 3;
        }

        return status;
    }
}
