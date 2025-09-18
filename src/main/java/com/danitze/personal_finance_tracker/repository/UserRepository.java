package com.danitze.personal_finance_tracker.repository;

import com.danitze.personal_finance_tracker.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(@NotNull String email);

    Optional<User> findByEmail(String email);

    @NotNull Page<User> findAll(@NotNull Pageable pageable);
}
