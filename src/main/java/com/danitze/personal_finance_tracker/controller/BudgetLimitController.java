package com.danitze.personal_finance_tracker.controller;

import com.danitze.personal_finance_tracker.dto.BudgetLimitDto;
import com.danitze.personal_finance_tracker.dto.UpdateBudgetLimitDto;
import com.danitze.personal_finance_tracker.service.BudgetLimitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budget-limits")
public class BudgetLimitController {

    private final BudgetLimitService budgetLimitService;

    @Autowired
    public BudgetLimitController(BudgetLimitService budgetLimitService) {
        this.budgetLimitService = budgetLimitService;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @budgetLimitService.isOwner(#id, principal.username)")
    public ResponseEntity<BudgetLimitDto> updateBudgetLimit(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBudgetLimitDto updateBudgetLimitDto
    ) {
        return ResponseEntity.ok(budgetLimitService.updateBudgetLimit(id, updateBudgetLimitDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @budgetLimitService.isOwner(#id, principal.username)")
    public ResponseEntity<Void> deleteBudgetLimit(@PathVariable Long id) {
        budgetLimitService.deleteBudgetLimit(id);
        return ResponseEntity.noContent().build();
    }

}
