package com.danitze.personal_finance_tracker.dto;

import com.danitze.personal_finance_tracker.entity.BudgetLimit;
import org.springframework.stereotype.Component;

@Component
public class BudgetLimitMapper {

    public BudgetLimitDto toDto(BudgetLimit budgetLimit) {
        BudgetLimitDto budgetLimitDto = new BudgetLimitDto();
        budgetLimitDto.setId(budgetLimit.getId());
        budgetLimitDto.setCategory(budgetLimit.getCategory());
        budgetLimitDto.setAmount(budgetLimit.getAmount());
        return budgetLimitDto;
    }

}
