package com.epam.lab.dao;

import com.epam.lab.entity.Role;

import java.util.List;

public interface RoleDao {
    void assignRoleToUser(Role role);
    void deleteUserRole(Role role);
    List<Role> getUserRoles(long userId);
}
