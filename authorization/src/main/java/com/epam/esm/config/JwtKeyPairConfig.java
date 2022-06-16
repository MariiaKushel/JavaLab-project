package com.epam.esm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Configuration
public class JwtKeyPairConfig {

    private static final String ENCRYPT_TYPE = "RSA";
    private static final int KEY_SIZE = 2048;

    @Bean
    public KeyPair keyPairBean() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(ENCRYPT_TYPE);
        gen.initialize(KEY_SIZE);
        return gen.generateKeyPair();
    }
}
