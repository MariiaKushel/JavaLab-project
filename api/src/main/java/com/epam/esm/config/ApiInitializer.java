package com.epam.esm.config;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Class register a DispatcherServlet and use configuration.
 */
public class ApiInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    private static final String SERVLET_MAPPER = "/";

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{DataBaseConfigurationProd.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{SERVLET_MAPPER};
    }

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        WebApplicationContext context = super.createRootApplicationContext();
        ((ConfigurableEnvironment) (context.getEnvironment())).addActiveProfile("prod");
        return context;
    }
}
