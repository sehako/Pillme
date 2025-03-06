package com.americanstartup.pillme.management.domain.type;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RegistrationType {
    MY_SELF("myself"),
    ADDED("added"),
    ADD("add");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
