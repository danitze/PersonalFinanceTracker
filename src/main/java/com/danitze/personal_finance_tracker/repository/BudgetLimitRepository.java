package com.danitze.personal_finance_tracker.repository;

import com.danitze.personal_finance_tracker.entity.Account;
import com.danitze.personal_finance_tracker.entity.BudgetLimit;
import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetLimitRepository extends JpaRepository<BudgetLimit, Long> {

    boolean existsByAccountAndCategory(Account account, TransactionCategory transactionCategory);

    List<BudgetLimit> findAllByAccount(Account account);
}
