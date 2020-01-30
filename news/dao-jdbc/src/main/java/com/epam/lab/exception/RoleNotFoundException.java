package com.epam.lab.exception;

import com.epam.lab.model.Role;

public class RoleNotFoundException extends RuntimeException {

    private Role role;

    public RoleNotFoundException(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
