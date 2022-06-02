package com.epam.esm.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Class represents base JWT properties from file "jwt.properties".
 */
@ConfigurationProperties(prefix = "jwt")
@PropertySource("classpath:jwt.properties")
@Component
@Data
public class JwtProperty {

    private String secret;
    private int expiration;
    private String cookieName;
    private String rolePropertyName;
    private String userIdPropertyName;

}
