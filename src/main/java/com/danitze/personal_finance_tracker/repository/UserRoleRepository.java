package com.danitze.personal_finance_tracker.repository;

import com.danitze.personal_finance_tracker.entity.UserRole;
import com.danitze.personal_finance_tracker.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByRole(Role role);

    Set<UserRole> findAllByRoleIn(Set<Role> roles);

}
