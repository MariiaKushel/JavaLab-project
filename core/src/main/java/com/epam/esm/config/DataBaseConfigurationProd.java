package com.epam.esm.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Data base configuration class
 */

@Configuration
@ComponentScan({"com.epam.esm.dao", "com.epam.esm.service"})
@PropertySource("classpath:db.properties")
@EnableTransactionManagement
@Profile("prod")
public class DataBaseConfigurationProd {

    /**
     * Method create HikariConfig for connection to database and set pool size
     * @param user database login
     * @param password database password
     * @param url database url
     * @param driver database connection driver
     * @param poolSize connection pool size
     * @return Bean of HikariConfig
     */
    @Bean
    public HikariConfig hikariConfig(@Value("${db.user}") String user,
                                     @Value("${db.password}") String password,
                                     @Value("${db.url}") String url,
                                     @Value("${db.driver}") String driver,
                                     @Value("${pool.size}") int poolSize) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setDriverClassName(driver);
        hikariConfig.setMaximumPoolSize(poolSize);
        return hikariConfig;
    }

    /**
     * Method create HikariDataSource based HikariConfig
     * @param hikariConfig Hikari configuration
     * @return Bean of HikariDataSource
     */
    @Bean
    public HikariDataSource hikariDataSource(HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    /**
     * Method create JdbcTemplate based HikariDataSource
     * @param hikariDataSource Hikari data source
     * @return Bean of JdbcTemplate
     */
    @Bean
    public JdbcTemplate jdbcTemplate(HikariDataSource hikariDataSource) {
        return new JdbcTemplate(hikariDataSource);
    }

    /**
     * Method create DataSourceTransactionManager based HikariDataSource
     * @param hikariDataSource Hikari data source
     * @return Bean of DataSourceTransactionManager
     */
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager (HikariDataSource hikariDataSource){
        return new DataSourceTransactionManager(hikariDataSource);
    }
}
