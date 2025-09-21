package com.danitze.personal_finance_tracker.exception;

import com.danitze.personal_finance_tracker.exception.account.AccountNotFoundException;
import com.danitze.personal_finance_tracker.exception.auth.EmailAlreadyUsedException;
import com.danitze.personal_finance_tracker.exception.auth.RefreshTokenExpiredException;
import com.danitze.personal_finance_tracker.exception.auth.RefreshTokenNotFoundException;
import com.danitze.personal_finance_tracker.exception.budget.BudgetLimitAlreadyUsedException;
import com.danitze.personal_finance_tracker.exception.budget.BudgetLimitNotFoundException;
import com.danitze.personal_finance_tracker.exception.notification.NotificationNotFoundException;
import com.danitze.personal_finance_tracker.exception.transaction.TransactionNotFoundException;
import com.danitze.personal_finance_tracker.exception.user.UserNotFoundException;
import com.danitze.personal_finance_tracker.exception.user.UserRoleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleEmailAlreadyUsedException(EmailAlreadyUsedException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFoundException(UserNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAccountNotFoundException(AccountNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleTransactionNotFoundException(TransactionNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleRefreshTokenExpiredException(RefreshTokenExpiredException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(UserRoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRefreshTokenExpiredException(UserRoleNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return errors;
    }

    @ExceptionHandler(BudgetLimitAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBudgetLimitAlreadyUsedException(BudgetLimitAlreadyUsedException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(BudgetLimitNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBudgetLimitNotFoundException(BudgetLimitNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotificationNotFoundException(NotificationNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

}
