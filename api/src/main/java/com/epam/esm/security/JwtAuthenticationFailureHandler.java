package com.epam.esm.security;

import com.epam.esm.advicer.ExceptionResponse;
import com.epam.esm.exception.CustomAuthenticationException;
import com.epam.esm.exception.CustomErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class represent custom authentication failure handler.
 */
@Component
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper mapper;

    @Autowired
    public JwtAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) {
        try {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            if (exception instanceof BadCredentialsException) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                this.mapper.writeValue(response.getWriter(),
                        new ExceptionResponse("Invalid username or password",
                        CustomErrorCode.NOT_VALID_AUTH_DATA.getCode()));
            } else if (exception instanceof CustomAuthenticationException) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                this.mapper.writeValue(response.getWriter(),
                        new ExceptionResponse("User already authenticated, logout first",
                        CustomErrorCode.ALREADY_AUTH.getCode()));
            }
        } catch (Exception e) {
            throw new AuthenticationServiceException("");
        }
    }
}
