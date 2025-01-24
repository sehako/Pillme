package com.ssafy.pillme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TestRequest(
        @NotBlank
        String name,
        @NotNull
        Integer num
) {
}
