package com.epam.esm.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Class represent application runner.
 */
@SpringBootApplication(scanBasePackages = "com.epam.esm")
@EnableConfigurationProperties
public class ApiConfig {

    public static void main(String[] args) {
        SpringApplication.run(ApiConfig.class, args);
    }

}
