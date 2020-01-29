package com.epam.lab.exception;

import com.epam.lab.entity.Role;

public class RoleAlreadyExistException extends RuntimeException {

    private Role role;

    public RoleAlreadyExistException(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
