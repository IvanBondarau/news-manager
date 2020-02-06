package com.epam.lab.dao;

import com.epam.lab.exception.ResourceNotFoundException;
import com.epam.lab.exception.RoleAlreadyExistException;
import com.epam.lab.model.Role;

import javax.sql.DataSource;
import java.util.List;

public interface RoleDao {
    void assignRoleToUser(Role role) throws RoleAlreadyExistException;
    void deleteUserRole(Role role) throws ResourceNotFoundException;
    List<Role> getUserRoles(long userId);

}
