package com.danitze.personal_finance_tracker.seed;

import com.danitze.personal_finance_tracker.entity.UserRole;
import com.danitze.personal_finance_tracker.entity.enums.Role;
import com.danitze.personal_finance_tracker.repository.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder {

    private final UserRoleRepository userRoleRepository;

    @Autowired
    public RoleSeeder(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @PostConstruct
    public void seedRoles() {
        for (Role role : Role.values()) {
            UserRole userRole = UserRole.builder()
                    .role(role)
                    .build();
            userRoleRepository.save(userRole);
        }
    }

}
