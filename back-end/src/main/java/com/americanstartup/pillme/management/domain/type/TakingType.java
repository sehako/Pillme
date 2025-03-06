package com.americanstartup.pillme.management.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.management.application.exception.InvalidTimeSelectException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TakingType {
    MORNING("morning"),
    LUNCH("lunch"),
    DINNER("dinner"),
    SLEEP("sleep"),
    ;

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TakingType of(String value) {
        for (TakingType t : TakingType.values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }

        throw new InvalidTimeSelectException(ErrorCode.INVALID_TIME_REQUEST);
    }
}
