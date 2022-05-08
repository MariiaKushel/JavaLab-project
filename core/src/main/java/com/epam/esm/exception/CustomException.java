package com.epam.esm.exception;

/**
 * Custom exception class
 */
public class CustomException extends Exception{

    private CustomErrorCode customErrorCode;

    public CustomException(String message, CustomErrorCode customErrorCode) {
        super(message);
        this.customErrorCode = customErrorCode;
    }

    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Exception cause) {
        super(message, cause);
    }

    public CustomException(Exception cause) {
        super(cause);
    }

    public CustomErrorCode getCustomErrorCode() {
        return customErrorCode;
    }

}
