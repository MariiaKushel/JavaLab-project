package com.epam.esm.controller;

import com.epam.esm.service.CustomTagService;
import com.epam.esm.service.GiftCertificateService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
    @Bean
    public CustomTagService customTagService() {
        return Mockito.mock(CustomTagService.class);
    }

    @Bean
    GiftCertificateService giftCertificateService() {
        return Mockito.mock(GiftCertificateService.class);
    }
}
