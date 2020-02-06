package com.epam.lab.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component(value = "dataSourceHolder")
public class DataSourceHolder {

    private DataSource dataSource;
    private Lock lock = new ReentrantLock();

    public void init() {
        HikariConfig config = new HikariConfig("/database.properties");
        /*config.setJdbcUrl("jdbc:postgresql://localhost:60666/newsManager");
        config.setUsername("newsManager");
        config.setPassword("12345");
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);*/
        dataSource = new HikariDataSource(config);
    }

    public DataSourceHolder() {


    }

    public DataSource getDataSource() {

        if (dataSource == null) {
            lock.lock();
            try {
                if (dataSource == null) {
                    init();
                }
            } finally {
                lock.unlock();
            }
        }
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
