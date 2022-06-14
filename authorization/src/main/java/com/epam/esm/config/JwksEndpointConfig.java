package com.epam.esm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;

@Configuration
public class JwksEndpointConfig extends AuthorizationServerSecurityConfiguration {

    private static final String JWKS_URL = "/.well-known/jwks.json";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http
                .requestMatchers()
                .mvcMatchers(JWKS_URL)
                .and()
                .authorizeRequests()
                .mvcMatchers(JWKS_URL).permitAll();
    }
}