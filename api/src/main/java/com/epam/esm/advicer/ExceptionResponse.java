package com.epam.esm.advicer;

/**
 * Class represents the body of exception response.
 */
public class ExceptionResponse {

    private String errorMessage;
    private int errorCode;

    public ExceptionResponse(String errorMessage, int errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
