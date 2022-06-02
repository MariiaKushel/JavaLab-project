package com.epam.esm.security;

import com.epam.esm.advicer.ExceptionResponse;
import com.epam.esm.exception.CustomErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class represent custom authentication entry point.
 */
@Component
public class JwtAuthenticationEntryPoint  implements AuthenticationEntryPoint {

    private ObjectMapper mapper;

    @Autowired
    public JwtAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        this.mapper.writeValue(response.getWriter(),
                new ExceptionResponse("Need authentication or authentication is not possible.",
                CustomErrorCode.NEED_AUTH_OR_NOT_POSSIBLE.getCode()));
    }
}
