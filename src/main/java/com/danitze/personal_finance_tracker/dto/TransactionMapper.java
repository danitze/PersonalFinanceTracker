package com.danitze.personal_finance_tracker.dto;

import com.danitze.personal_finance_tracker.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionDto toDto(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setAccountId(transaction.getAccount().getId());
        transactionDto.setCategory(transaction.getCategory());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setType(transaction.getType());
        transactionDto.setTxDate(transaction.getTxDate());
        transactionDto.setNote(transaction.getNote());
        transactionDto.setCreatedAt(transaction.getCreatedAt());
        return transactionDto;
    }

}
