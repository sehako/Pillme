package com.americanstartup.pillme.admin.application.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
