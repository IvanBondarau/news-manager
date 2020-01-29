package com.epam.lab.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {

    private DataSource dataSource;

    @Bean
    public DataSource getDataSource() {
        String path = JdbcConfig.class.getResource("/database.properties").getPath();
        HikariConfig hikariConfig = new HikariConfig(path);
        dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }
}
