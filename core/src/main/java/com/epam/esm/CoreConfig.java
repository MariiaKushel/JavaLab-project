package com.epam.esm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.epam.esm")
public class CoreConfig {

    public static void main(String[] args) {
        SpringApplication.run(CoreConfig.class, args);
    }

}
