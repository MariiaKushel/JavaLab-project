package com.epam.esm.advicer;

import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Class represent spring advice to catch exceptions and wrap them into custom ExceptionResponse.
 */
@RestControllerAdvice
public class CustomExceptionAdvice {

    private ResourceBundleMessageSource messageSource;

    @Autowired
    public CustomExceptionAdvice(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Method catches CustomException and generates response entity.
     *
     * @param e - CustomException
     * @return - response entity consist body - ExceptionResponse and HttpStatus
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException e) {
        int errorCode = e.getCustomErrorCode().getCode();
        String message = messageSource.getMessage(String.valueOf(errorCode),
                null, LocaleContextHolder.getLocale()) + e.getMessage();
        HttpStatus httpStatus = e.getCustomErrorCode().getHttpStatus();
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }

    /**
     * Method catches CustomException and generates response entity.
     *
     * @param e - Exception
     * @return - response entity consist body - ExceptionResponse and HttpStatus
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleInternalServerException(Exception e) {
        int errorCode = CustomErrorCode.INTERNAL_SERVER_EXCEPTION.getCode();
        String message = messageSource.getMessage(String.valueOf(errorCode),
                null, LocaleContextHolder.getLocale())
                + e.getMessage()
                + e.getCause();
        HttpStatus httpStatus = CustomErrorCode.INTERNAL_SERVER_EXCEPTION.getHttpStatus();
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }

    /**
     * Method catches HttpRequestMethodNotSupportedException and generates response entity.
     *
     * @param e - HttpRequestMethodNotSupportedException
     * @return - response entity consist body - ExceptionResponse and HttpStatus
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        int errorCode = CustomErrorCode.METHOD_NOT_SUPPORTED.getCode();
        String message = messageSource.getMessage(String.valueOf(errorCode),
                null, LocaleContextHolder.getLocale()) + e.getMethod() + ".";
        HttpStatus httpStatus = CustomErrorCode.METHOD_NOT_SUPPORTED.getHttpStatus();
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }

    /**
     * Method catches MethodArgumentTypeMismatchException and generates response entity.
     *
     * @param e - MethodArgumentTypeMismatchException
     * @return - response entity consist body - ExceptionResponse and HttpStatus
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        int errorCode = CustomErrorCode.TYPE_MISMATCH.getCode();
        String message = messageSource.getMessage(String.valueOf(errorCode),
                null, LocaleContextHolder.getLocale());
        HttpStatus httpStatus = CustomErrorCode.TYPE_MISMATCH.getHttpStatus();
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }

    /**
     * Method catches HttpMessageNotReadableException and generates response entity.
     *
     * @param e - HttpMessageNotReadableException
     * @return - response entity consist body - ExceptionResponse and HttpStatus
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        int errorCode = CustomErrorCode.NOT_READABLE.getCode();
        String message = messageSource.getMessage(String.valueOf(errorCode),
                null, LocaleContextHolder.getLocale());
        HttpStatus httpStatus = CustomErrorCode.NOT_READABLE.getHttpStatus();
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }

    /**
     * Method catches HttpMediaTypeNotSupportedException and generates response entity.
     *
     * @param e - HttpMediaTypeNotSupportedException
     * @return - response entity consist body - ExceptionResponse and HttpStatus
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
            HttpMediaTypeNotSupportedException e) {
        int errorCode = CustomErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode();
        String message = messageSource.getMessage(String.valueOf(errorCode),
                null, LocaleContextHolder.getLocale());
        HttpStatus httpStatus = CustomErrorCode.UNSUPPORTED_MEDIA_TYPE.getHttpStatus();
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }

    /**
     * Method catches MissingServletRequestParameterException and generates response entity.
     *
     * @param e - MissingServletRequestParameterException
     * @return - response entity consist body - ExceptionResponse and HttpStatus
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        int errorCode = CustomErrorCode.MISSING_PARAMETER.getCode();
        String message = messageSource.getMessage(String.valueOf(errorCode),
                null, LocaleContextHolder.getLocale()) + e.getParameterName() + ".";
        HttpStatus httpStatus = CustomErrorCode.MISSING_PARAMETER.getHttpStatus();
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, errorCode);
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }
}
