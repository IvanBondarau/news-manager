package com.epam.lab.exception;

import com.epam.lab.model.Role;

public class RoleAlreadyExistException extends Exception {

    private Role role;

    public RoleAlreadyExistException(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
