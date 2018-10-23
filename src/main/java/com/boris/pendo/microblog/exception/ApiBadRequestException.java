package com.boris.pendo.microblog.exception;

import org.springframework.http.HttpStatus;

public class ApiBadRequestException extends ApiServerException {
    private static final int STATUS = HttpStatus.BAD_REQUEST.value();

    public ApiBadRequestException(String message, String transactionId, Throwable cause) {
        super(STATUS, message, transactionId, cause);
    }

    public ApiBadRequestException(String message, String transactionId) {
        super(STATUS, message, transactionId);
    }

    public ApiBadRequestException(String message, Throwable cause) {
        super(STATUS, message, cause);
    }

    public ApiBadRequestException(String message) {
        super(STATUS, message);
    }

}
