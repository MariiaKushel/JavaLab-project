package com.epam.esm.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * Custom authentication exception class
 */
public class CustomAuthenticationException extends AuthenticationServiceException {

    private CustomErrorCode customErrorCode;

    public CustomAuthenticationException(String message, CustomErrorCode customErrorCode) {
        super(message);
        this.customErrorCode = customErrorCode;
    }

    public CustomErrorCode getCustomErrorCode() {
        return customErrorCode;
    }
}
