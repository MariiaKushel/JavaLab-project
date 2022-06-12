package com.epam.esm.util;

import com.epam.esm.enumeration.UserRole;
import com.epam.esm.properties.JwtProperty;
import io.jsonwebtoken.Jwts;

import java.util.Base64;

/**
 * Util-class helps to extract some data part from JWT.
 */

public class JwtDecoder {

    /**
     * Method to extract user id from JWT.
     * @param jwt JWToken
     * @param jwtProperty jwt properties
     * @return user id as Long
     */
    public static Long decodeUserId(String jwt, JwtProperty jwtProperty) {
        return Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(jwtProperty.getSecret().getBytes()))
                .parseClaimsJws(jwt)
                .getBody()
                .get(jwtProperty.getUserIdPropertyName(), Long.class);
    }

    /**
     * Method to extract user role from JWT.
     * @param jwt JWToken
     * @param jwtProperty jwt properties
     * @return user role
     */
    public static UserRole decodeRole(String jwt, JwtProperty jwtProperty) {
        String roleAsString = Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(jwtProperty.getSecret().getBytes()))
                .parseClaimsJws(jwt)
                .getBody()
                .get(jwtProperty.getRolePropertyName(), String.class);
        return UserRole.valueOf(roleAsString);
    }
}
