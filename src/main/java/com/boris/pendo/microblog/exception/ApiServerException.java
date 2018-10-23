package com.boris.pendo.microblog.exception;

/**
 * Base class for all server exceptions. These are considered server errors and need error logging level.
 */
public class ApiServerException extends ApiException {

    public ApiServerException(int statusCode, String message, String transactionId, Throwable cause) {
        super(statusCode, message, transactionId, cause);
    }

    public ApiServerException(int statusCode, String message, String transactionId) {
        super(statusCode, message, transactionId);
    }

    public ApiServerException(int statusCode, String message, Throwable cause) {
        super(statusCode, message, cause);
    }

    public ApiServerException(int statusCode, String message) {
        super(statusCode, message);
    }
}
