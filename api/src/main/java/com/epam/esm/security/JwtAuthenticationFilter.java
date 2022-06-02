package com.epam.esm.security;

import com.epam.esm.exception.CustomAuthenticationException;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.properties.JwtProperty;
import com.epam.esm.service.dto.UsernamePasswordDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;

/**
 * Class represent custom username password authentication filter.
 */
@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private ObjectMapper mapper;
    private JwtProperty jwtProperty;

    @Autowired
    public JwtAuthenticationFilter(ObjectMapper mapper, JwtProperty jwtProperty) {
        this.mapper = mapper;
        this.jwtProperty = jwtProperty;
    }

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Autowired
    @Override
    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        super.setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            UsernamePasswordDto user;
            try {
                user = this.mapper.readValue(request.getReader(), UsernamePasswordDto.class);
                if (user != null && !user.getUsername().isEmpty()) {
                    if (!user.getPassword().isEmpty()) {
                        UsernamePasswordAuthenticationToken token
                                = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
                        return this.getAuthenticationManager().authenticate(token);
                    }
                }
            } catch (IOException e) {
            }
            throw new BadCredentialsException("");
        } else {
            throw new CustomAuthenticationException("", CustomErrorCode.ALREADY_AUTH);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) {
        try {
            CustomUserDetails authUser = (CustomUserDetails) authResult.getPrincipal();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            }

            Claims claims = Jwts.claims().setSubject(authUser.getUsername());
            claims.put(this.jwtProperty.getUserIdPropertyName(), authUser.getUserId());
            String role = authUser.getRoleJWT();
            if (!role.isEmpty()) {
                claims.put(this.jwtProperty.getRolePropertyName(), role);
            }
            String token = Jwts.builder()
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS512,
                            Base64.getEncoder().encodeToString(this.jwtProperty.getSecret().getBytes()))
                    .compact();
            Cookie jwtCookie = new Cookie(this.jwtProperty.getCookieName(), token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge(this.jwtProperty.getExpiration());
            jwtCookie.setPath("/");
            jwtCookie.setSecure(true);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);

            response.addCookie(jwtCookie);
        } catch (Exception e) {
            throw new AuthenticationServiceException("");
        }
    }
}