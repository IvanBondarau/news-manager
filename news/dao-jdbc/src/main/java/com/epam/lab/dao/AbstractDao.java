package com.epam.lab.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public abstract class AbstractDao {

    protected JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    protected void setDataSource(DataSource dataSource) {
        if (dataSource == null) {
            throw new RuntimeException("HELL");
        }
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
    }
}
