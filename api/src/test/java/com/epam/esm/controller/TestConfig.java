package com.epam.esm.controller;

import com.epam.esm.util.impl.AdminCollectionLinkCreator;
import com.epam.esm.util.impl.AdminSingleEntityLinkCreator;
import com.epam.esm.util.impl.CommonCollectionLinkCreator;
import com.epam.esm.util.impl.CommonSingleEntityLinkCreator;
import com.epam.esm.util.impl.UserCollectionLinkCreator;
import com.epam.esm.util.impl.UserSingleEntityLinkCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
public class TestConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
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

    @Bean
    public JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        return grantedAuthoritiesConverter;
    }
}
