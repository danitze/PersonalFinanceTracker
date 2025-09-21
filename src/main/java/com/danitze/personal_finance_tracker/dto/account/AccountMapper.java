package com.danitze.personal_finance_tracker.dto.account;

import com.danitze.personal_finance_tracker.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDto toDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(account.getId());
        accountDto.setUserId(account.getUser().getId());
        accountDto.setName(account.getName());
        accountDto.setCurrency(account.getCurrency());
        accountDto.setBalance(account.getBalance());
        accountDto.setCreatedAt(account.getCreatedAt());
        return accountDto;
    }

}
