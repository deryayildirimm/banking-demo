package com.example.demo.exception;

import com.example.demo.common.ErrorMessages;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(Long id) {
        super(ErrorMessages.ACCOUNT_NOT_EXISTS + id);
    }
}
