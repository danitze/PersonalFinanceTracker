package com.danitze.personal_finance_tracker.exception.budget;

import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;

public class BudgetLimitAlreadyUsedException extends RuntimeException {

    public BudgetLimitAlreadyUsedException(TransactionCategory transactionCategory) {
        super("Budget limit is already used for transaction category " + transactionCategory);
    }

}
