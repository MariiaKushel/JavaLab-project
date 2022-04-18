package com.epam.esm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan({"com.epam.esm.dao", "com.epam.esm.service"})
@EnableTransactionManagement
@Profile("test")
public class DataBaseConfigurationTest {

    /**
     * Method create EmbeddedDatabase data source
     * @return Bean of EmbeddedDatabase
     */
    @Bean (destroyMethod = "shutdown")
    public EmbeddedDatabase embeddedDatabase() {
        EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addDefaultScripts()
                .setType(EmbeddedDatabaseType.H2)
                .build();
        return embeddedDatabase;
    }

    /**
     * Method create JdbcTemplate based EmbeddedDatabase
     * @param embeddedDatabase embedded database data source
     * @return Bean of JdbcTemplate
     */
    @Bean
    public JdbcTemplate jdbcTemplate(EmbeddedDatabase embeddedDatabase) {
        return new JdbcTemplate(embeddedDatabase);
    }

    /**
     * Method create DataSourceTransactionManager based EmbeddedDatabase
     * @param embeddedDatabase embedded database data source
     * @return Bean of DataSourceTransactionManager
     */
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager (EmbeddedDatabase embeddedDatabase){
        return new DataSourceTransactionManager(embeddedDatabase);
    }

}
