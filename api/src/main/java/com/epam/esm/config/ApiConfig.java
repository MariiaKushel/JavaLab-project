package com.epam.esm.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Class represent application runner.
 */
@SpringBootApplication(scanBasePackages = "com.epam.esm")
public class ApiConfig {

    public static void main(String[] args) {
        SpringApplication.run(ApiConfig.class, args);
    }

}
