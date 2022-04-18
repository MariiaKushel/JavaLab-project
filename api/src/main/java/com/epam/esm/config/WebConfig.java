package com.epam.esm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.ArrayList;
import java.util.List;

/**
 * Web configuration class
 */

@Configuration
@ComponentScan({"com.epam.esm.controller", "com.epam.esm.advicer"})
@EnableWebMvc
public class WebConfig {

    /**
     * Method create view that renders JSON content.
     * @return Bean of MappingJackson2JsonView
     */
    @Bean
    public MappingJackson2JsonView jsonView() {
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        jsonView.setPrettyPrint(true);
        return jsonView;
    }

    /**
     * Method set jsonView as a default view
     * @return Bean of ViewResolver
     */
    @Bean
    public ViewResolver contentNegotiatingViewResolver() {
        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        List<View> viewList = new ArrayList<>();
        viewList.add(jsonView());
        viewResolver.setDefaultViews(viewList);
        return viewResolver;
    }
}