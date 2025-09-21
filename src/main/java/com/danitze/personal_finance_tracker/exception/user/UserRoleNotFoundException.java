package com.danitze.personal_finance_tracker.exception.user;

import com.danitze.personal_finance_tracker.entity.enums.Role;

public class UserRoleNotFoundException extends RuntimeException {

    public UserRoleNotFoundException(Role role) {
        super("Role " + role.name() + " not found");
    }

}
