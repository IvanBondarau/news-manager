package com.epam.lab.dao;

import com.epam.lab.dao.JdbcRoleDao;
import com.epam.lab.dao.RoleDao;
import com.epam.lab.exception.RoleAlreadyExistException;
import com.epam.lab.exception.RoleNotFoundException;
import com.epam.lab.model.Role;
import com.epam.lab.model.User;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class JdbcRoleDaoTest {

    private static EmbeddedDatabase embeddedDatabase;
    private RoleDao roleDao;
    private JdbcTemplate jdbcTemplate;
    private User testUser;

    @BeforeClass
    public static void initDatabase() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @AfterClass
    public static void shutdownDatabase() {
        embeddedDatabase.shutdown();
    }

    @Before
    public void init() {
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        roleDao = new JdbcRoleDao(embeddedDatabase);

        testUser = new User(1, "test", "test", "test", "test");
        jdbcTemplate.update("INSERT INTO public.users VALUES(?, ?, ?, ?, ?)",
                testUser.getId(),
                testUser.getName(),
                testUser.getLogin(),
                testUser.getLogin(),
                testUser.getPassword()
                );
    }

    @After
    public void clear() {
        jdbcTemplate.update("DELETE FROM roles");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    public void assignShouldBeValid() {
        Role role = new Role(testUser.getId(), "role_name");
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
        Role role = new Role(testUser.getId(), null);
        roleDao.assignRoleToUser(role);
    }

    @Test(expected = RoleAlreadyExistException.class)
    public void assignRoleTwice() {
        Role role = new Role(testUser.getId(), "test");
        roleDao.assignRoleToUser(role);
        roleDao.assignRoleToUser(role);
    }

    @Test
    public void deleteShouldBeValid() {
        Role role = new Role(testUser.getId(), "role_name");

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
        Role role = new Role(testUser.getId(), "test");
        roleDao.deleteUserRole(role);
    }

    @Test(expected = RoleNotFoundException.class)
    public void deleteRoleNotExist() {
        Role role = new Role(testUser.getId(), "test");

        jdbcTemplate.update("INSERT INTO public.roles VALUES(?, ?)",
                role.getUserId(), role.getName());

        role.setName("new name");
        roleDao.deleteUserRole(role);
    }


    @Test
    public void getUserRolesShouldBeValid() {
        Role role1 = new Role(testUser.getId(), "role_name1");
        Role role2 = new Role(testUser.getId(), "role_name2");

        jdbcTemplate.update("INSERT INTO public.roles VALUES(?, ?)",
                role1.getUserId(), role1.getName());

        jdbcTemplate.update("INSERT INTO public.roles VALUES(?, ?)",
                role2.getUserId(), role2.getName());


        List<Role> loadedRoles = roleDao.getUserRoles(testUser.getId());

        assertTrue(loadedRoles.contains(role1));
        assertTrue(loadedRoles.contains(role2));

    }

    @Test
    public void getUserRolesEmpty() {
        List<Role> loadedRoles = roleDao.getUserRoles(4);

        assertEquals(0, loadedRoles.size());
    }


}