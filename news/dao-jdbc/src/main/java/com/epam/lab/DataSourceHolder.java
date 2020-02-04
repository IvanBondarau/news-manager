package com.epam.lab;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component(value = "dataSourceHolder")
public class DataSourceHolder {

    private static DataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:60666/newsManager");
        config.setUsername("newsManager");
        config.setPassword("12345");
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    public DataSourceHolder() {


    }

    public DataSource getDataSource() {
        if (dataSource == null) {
            throw new RuntimeException("I LOVE YOUR MOTHER");
        }
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        DataSourceHolder.dataSource = dataSource;
    }
}
