package com.boris.pendo.microblog.exception;

import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

public class ApiException extends RuntimeException {

    @Getter
    private final int statusCode;

    @NonNull
    @Getter
    private final String eventId;

    public ApiException(int statusCode, String message, String eventId, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.eventId = eventId;
    }

    public ApiException(int statusCode, String message, String eventId) {
        this(statusCode, message, eventId, null);
    }

    public ApiException(int statusCode, String message, Throwable cause) {
        this(statusCode, message, UUID.randomUUID().toString(), cause);
    }

    public ApiException(int statusCode, String message) {
        this(statusCode, message, UUID.randomUUID().toString());
    }

}
