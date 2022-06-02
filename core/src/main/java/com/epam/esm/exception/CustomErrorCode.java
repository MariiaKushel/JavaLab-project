package com.epam.esm.exception;

import org.springframework.http.HttpStatus;

/**
 * Enum represents custom error codes
 */
public enum CustomErrorCode {

    NOT_VALID_DATA(40001, HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH(40002, HttpStatus.BAD_REQUEST),
    NOT_READABLE(40003, HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER(40004, HttpStatus.BAD_REQUEST),
    NEED_AUTH_OR_NOT_POSSIBLE(40101, HttpStatus.UNAUTHORIZED),
    NOT_VALID_AUTH_DATA(40102, HttpStatus.UNAUTHORIZED),
    ALREADY_AUTH(40301, HttpStatus.FORBIDDEN),
    FORBIDDEN_RESOURCE(40302, HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND(40401, HttpStatus.NOT_FOUND),
    METHOD_NOT_SUPPORTED(40501, HttpStatus.METHOD_NOT_ALLOWED),

    RESOURCE_ALREADY_EXIST(40901, HttpStatus.CONFLICT),
    LINKED_TO_ANOTHER_RESOURCE(40902, HttpStatus.CONFLICT),
    DIFFERENT_CONDITION(40903, HttpStatus.CONFLICT),
    UNSUPPORTED_MEDIA_TYPE(41501, HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    INTERNAL_SERVER_EXCEPTION(50001, HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final HttpStatus httpStatus;

    CustomErrorCode(int code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
