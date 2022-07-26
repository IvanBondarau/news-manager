package com.epam.lab.dao;

import com.epam.lab.configuration.DaoConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoConfig.class)
abstract public class AbstractDatabaseTest {

    @Autowired
    protected DataSource dataSource;
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Before
    public void initData() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("test_data.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
