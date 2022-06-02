package com.epam.esm.security;

import com.epam.esm.enumeration.AppRole;
import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomInvalidJWTokenException;
import com.epam.esm.properties.JwtProperty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Set;

/**
 * Class represent custom once per request filter.
 */
@Component
public class JwtValidationFilter extends OncePerRequestFilter {

    private JwtProperty jwtProperty;

    @Autowired
    public JwtValidationFilter(JwtProperty jwtProperty) {
        this.jwtProperty = jwtProperty;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Cookie jwtCookie = WebUtils.getCookie(request, jwtProperty.getCookieName());
        if (jwtCookie == null || jwtCookie.getValue() == null || jwtCookie.getValue().isEmpty()) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        } else {
            try {
                UsernamePasswordAuthenticationToken authenticationToken = this.getAuthentication(jwtCookie);
                if (authenticationToken == null) {
                    SecurityContextHolder.clearContext();
                } else {
                    authenticationToken.eraseCredentials();
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(context);
                }
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                throw e;
            }
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(Cookie jwtCookie) {
        String token = jwtCookie.getValue();
        Claims claims = this.validateToken(token);
        String username = claims.getSubject();
        if (username != null) {
            Long userId = claims.get(this.jwtProperty.getUserIdPropertyName(), Long.class);
            AppRole role = AppRole.valueOf(claims.get(this.jwtProperty.getRolePropertyName(), String.class));
            CustomUserDetails details = new CustomUserDetails(username, "", role, userId);
            return new UsernamePasswordAuthenticationToken(details, null, Set.of (new SimpleGrantedAuthority(role.name())));
        }
        return null;
    }

    private Claims validateToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(this.jwtProperty.getSecret().getBytes()))
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (SignatureException e) {
            throw new CustomInvalidJWTokenException("Invalid JWT signature", CustomErrorCode.NOT_VALID_DATA);
        } catch (MalformedJwtException e) {
            throw new CustomInvalidJWTokenException("Invalid JWT token", CustomErrorCode.NOT_VALID_DATA);
        } catch (ExpiredJwtException e) {
            throw new CustomInvalidJWTokenException("Expired JWT token", CustomErrorCode.NOT_VALID_DATA);
        } catch (UnsupportedJwtException e) {
            throw new CustomInvalidJWTokenException("Unsupported JWT token", CustomErrorCode.NOT_VALID_DATA);
        } catch (IllegalArgumentException e) {
            throw new CustomInvalidJWTokenException("JWT claims string is empty", CustomErrorCode.NOT_VALID_DATA);
        }
    }
}
