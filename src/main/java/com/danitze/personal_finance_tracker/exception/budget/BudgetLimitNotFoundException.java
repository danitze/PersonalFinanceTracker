package com.danitze.personal_finance_tracker.exception.budget;

public class BudgetLimitNotFoundException extends RuntimeException {

    public BudgetLimitNotFoundException() {
        super("Budget limit not found");
    }

}
