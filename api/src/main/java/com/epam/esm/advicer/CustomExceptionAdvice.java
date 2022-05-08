package com.epam.esm.advicer;

import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Class represent spring advice to catch custom exceptions.
 */
@ControllerAdvice
public class CustomExceptionAdvice {

    /**
     * Method catches custom exception and generates response entity.
     *
     * @param e - custom exception
     * @return - response entity consist body - ExceptionResponse and HttpStatus
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException e) {
        String message = e.getMessage();
        int errorCode = e.getCustomErrorCode().getCode();
        HttpStatus httpStatus = e.getCustomErrorCode().getHttpStatus();
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionResponse> handleInternalServerException(Exception e) {
//        String message = e.getMessage();
//        int errorCode = CustomErrorCode.INTERNAL_SERVER_EXCEPTION.getCode();
//        HttpStatus httpStatus = CustomErrorCode.INTERNAL_SERVER_EXCEPTION.getHttpStatus();
//        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
//        return new ResponseEntity<>(exceptionResponse, httpStatus);
//    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        String message = e.getMessage();
        int errorCode = CustomErrorCode.METHOD_NOT_SUPPORTED.getCode();
        HttpStatus httpStatus = CustomErrorCode.METHOD_NOT_SUPPORTED.getHttpStatus();
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        String message = "Can not convert argument to expected type.";
        int errorCode = CustomErrorCode.TYPE_MISMATCH.getCode();
        HttpStatus httpStatus = CustomErrorCode.TYPE_MISMATCH.getHttpStatus();
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        String message = "Can not read object from JSON.";
        int errorCode = CustomErrorCode.NOT_READABLE.getCode();
        HttpStatus httpStatus = CustomErrorCode.NOT_READABLE.getHttpStatus();
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }
}
