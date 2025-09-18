package com.danitze.personal_finance_tracker.repository;

import com.danitze.personal_finance_tracker.entity.Account;
import com.danitze.personal_finance_tracker.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Page<Account> findByUser(@NotNull User user, @NotNull Pageable pageable);

}
