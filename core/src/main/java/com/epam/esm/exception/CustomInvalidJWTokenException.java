package com.epam.esm.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * Custom invalid JWT exception class
 */
public class CustomInvalidJWTokenException extends AuthenticationServiceException {

    private CustomErrorCode customErrorCode;

    public CustomInvalidJWTokenException(String message, CustomErrorCode customErrorCode) {
        super(message);
        this.customErrorCode = customErrorCode;
    }

    public CustomErrorCode getCustomErrorCode() {
        return customErrorCode;
    }
}
