package com.epam.lab.dao;

import com.epam.lab.exception.RoleAlreadyExistException;
import com.epam.lab.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcRoleDao extends AbstractDao implements RoleDao {

    private static final String INSERT_STATEMENT =
            "INSERT INTO public.roles(user_id, role_name) VALUES(?, ?)";

    private static final String DELETE_STATEMENT =
            "DELETE FROM public.roles WHERE user_id = ? AND role_name = ?";

    private static final String SELECT_BY_USER_ID_STATEMENT =
            "SELECT user_id, role_name FROM public.roles WHERE user_id = ?";

    @Autowired
    public JdbcRoleDao(DataSourceHolder dataSourceHolder) {
        super.setDataSource(dataSourceHolder.getDataSource());
    }


    @Override
    public void assignRoleToUser(Role role) throws RoleAlreadyExistException {

        //Check if the role is already assigned to user
        List<Role> userRoles = getUserRoles(role.getUserId());
        if (userRoles.contains(role)) {
            throw new RoleAlreadyExistException(role);
        }

        jdbcTemplate.update(
                INSERT_STATEMENT,
                role.getUserId(),
                role.getName()
        );
    }

    @Override
    public void deleteUserRole(Role role)  {
        long deleted = jdbcTemplate.update(
                DELETE_STATEMENT,
                role.getUserId(),
                role.getName()
        );

        if (deleted != 1) {
            throw new RuntimeException("Role " + role.getName()
                    + " for user " + role.getUserId() + " not found");
        }
    }

    @Override
    public List<Role> getUserRoles(long userId) {
        return jdbcTemplate.query(
                SELECT_BY_USER_ID_STATEMENT,
                new Object[]{userId},
                new RoleMapper()
        );
    }

    private static final class RoleMapper implements RowMapper<Role> {

        @Override
        public Role mapRow(ResultSet resultSet, int i) throws SQLException {
            long userId = resultSet.getLong(1);
            String roleName = resultSet.getString(2);

            return new Role(userId, roleName);
        }
    }


}
