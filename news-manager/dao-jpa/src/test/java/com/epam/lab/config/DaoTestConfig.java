package com.epam.lab.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@ComponentScan(
        basePackages = {"com.epam.lab"}
)
@EnableTransactionManagement
public class DaoTestConfig {
    @Bean
    public EntityManagerFactory getEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("com.epam.lab.dao");
    }

    @Bean
    public PlatformTransactionManager getPlatformTransactionManager(EntityManagerFactory factory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(factory);
        return transactionManager;
    }

    @Bean
    @Scope("singleton")
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig("/database.properties");
        DataSource dataSource = new HikariDataSource(config);
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("schema.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Recreate datasource to fetch default schema updates
        return new HikariDataSource(config);
    }

    @Bean
    @Scope("prototype")
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


}
