package com.epam.lab.dao;

import com.epam.lab.configuration.JdbcConfig;
import com.epam.lab.entity.Role;
import com.epam.lab.exception.RoleAlreadyExistException;
import com.epam.lab.exception.RoleNotFoundException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class RoleDaoImplTest {

    private RoleDao roleDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void init() {
        String path = JdbcConfig.class.getResource("/database.properties").getPath();
        HikariConfig hikariConfig = new HikariConfig(path);
        DataSource dataSource = new HikariDataSource(hikariConfig);

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.roleDao = new RoleDaoImpl(dataSource);

        jdbcTemplate.execute("TRUNCATE TABLE public.roles");
    }

    @After
    public void clear() {
        jdbcTemplate.execute("TRUNCATE TABLE public.roles");
    }


    @Test
    public void assignShouldBeValid() {
        Role role = new Role(5, "role_name");
        roleDao.assignRoleToUser(role);

        List<Role> loadedRoles = jdbcTemplate.query(
                "SELECT user_id, role_name FROM public.roles WHERE user_id = ? AND role_name = ?",
                new Object[]{role.getUserId(), role.getName()},
                (resultSet, i) -> {
                    long userId = resultSet.getLong(1);
                    String name = resultSet.getString(2);
                    return new Role(userId, name);
                }
        );

        assertEquals(1, loadedRoles.size());
        assertEquals(role, loadedRoles.get(0));
    }

    @Test(expected = Exception.class)
    public void assignNullRoleName() {
        Role role = new Role(5, null);
        roleDao.assignRoleToUser(role);
    }

    @Test(expected = RoleAlreadyExistException.class)
    public void assignRoleTwice() {
        Role role = new Role(5, "test");
        roleDao.assignRoleToUser(role);
        roleDao.assignRoleToUser(role);
    }

    @Test
    public void deleteShouldBeValid() {
        Role role = new Role(4, "role_name");

        jdbcTemplate.update("INSERT INTO public.roles VALUES(?, ?)",
                role.getUserId(), role.getName());
        roleDao.deleteUserRole(role);

        List<Role> loadedRoles = jdbcTemplate.query(
                "SELECT user_id, role_name FROM public.roles WHERE user_id = ? AND role_name = ?",
                new Object[]{role.getUserId(), role.getName()},
                (resultSet, i) -> {
                    long userId = resultSet.getLong(1);
                    String name = resultSet.getString(2);
                    return new Role(userId, name);
                }
        );

        assertEquals(0, loadedRoles.size());
    }

    @Test(expected = RoleNotFoundException.class)
    public void deleteUserIdNotExist() {
        Role role = new Role(17, "test");
        roleDao.deleteUserRole(role);
    }

    @Test(expected = RoleNotFoundException.class)
    public void deleteRoleNotExist() {
        Role role = new Role(17, "test");

        jdbcTemplate.update("INSERT INTO public.roles VALUES(?, ?)",
                role.getUserId(), role.getName());

        role.setName("new name");
        roleDao.deleteUserRole(role);
    }


    @Test
    public void getUserRolesShouldBeValid() {
        Role role1 = new Role(4, "role_name1");
        Role role2 = new Role(4, "role_name2");
        Role role3 = new Role(5, "role_name1");

        jdbcTemplate.update("INSERT INTO public.roles VALUES(?, ?)",
                role1.getUserId(), role1.getName());

        jdbcTemplate.update("INSERT INTO public.roles VALUES(?, ?)",
                role2.getUserId(), role2.getName());

        jdbcTemplate.update("INSERT INTO public.roles VALUES(?, ?)",
                role3.getUserId(), role3.getName());


        List<Role> loadedRoles = roleDao.getUserRoles(4);

        assertTrue(loadedRoles.contains(role1));
        assertTrue(loadedRoles.contains(role2));

        loadedRoles = roleDao.getUserRoles(5);

        assertTrue(loadedRoles.contains(role3));
    }

    @Test
    public void getUserRolesEmpty() {
        List<Role> loadedRoles = roleDao.getUserRoles(4);

        assertEquals(0, loadedRoles.size());
    }


}