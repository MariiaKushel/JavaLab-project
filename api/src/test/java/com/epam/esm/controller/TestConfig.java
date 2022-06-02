package com.epam.esm.controller;

import com.epam.esm.properties.JwtProperty;
import com.epam.esm.security.CustomAccessDeniedHandler;
import com.epam.esm.security.JwtAuthenticationEntryPoint;
import com.epam.esm.security.JwtAuthenticationFailureHandler;
import com.epam.esm.security.SecurityService;
import com.epam.esm.service.UserService;
import com.epam.esm.util.impl.AdminCollectionLinkCreator;
import com.epam.esm.util.impl.AdminSingleEntityLinkCreator;
import com.epam.esm.util.impl.CommonCollectionLinkCreator;
import com.epam.esm.util.impl.CommonSingleEntityLinkCreator;
import com.epam.esm.util.impl.UserCollectionLinkCreator;
import com.epam.esm.util.impl.UserSingleEntityLinkCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties(prefix = "jwt")
@PropertySource("classpath:jwt.properties")
@Configuration
public class TestConfig {

    @Bean
    public JwtProperty jwtProperty() {
        return new JwtProperty();
    }

    @Bean
    public SecurityService securityService(UserService userServiceMock) {
        return new SecurityService(userServiceMock);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint(objectMapper());
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler(objectMapper());
    }

    @Bean
    public JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler() {
        return new JwtAuthenticationFailureHandler(objectMapper());
    }

    @Bean
    public UserSingleEntityLinkCreator userSingleEntityLinkCreator() {
        return new UserSingleEntityLinkCreator();
    }

    @Bean
    public UserCollectionLinkCreator userCollectionLinkCreator() {
        return new UserCollectionLinkCreator();
    }

    @Bean
    public AdminSingleEntityLinkCreator adminSingleEntityLinkCreator() {
        return new AdminSingleEntityLinkCreator();
    }

    @Bean
    public AdminCollectionLinkCreator adminCollectionLinkCreator() {
        return new AdminCollectionLinkCreator();
    }

    @Bean
    public CommonSingleEntityLinkCreator commonSingleEntityLinkCreator() {
        return new CommonSingleEntityLinkCreator();
    }

    @Bean
    public CommonCollectionLinkCreator commonCollectionLinkCreator() {
        return new CommonCollectionLinkCreator();
    }
}
