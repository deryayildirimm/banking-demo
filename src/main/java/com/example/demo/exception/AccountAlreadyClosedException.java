package com.example.demo.exception;

import com.example.demo.common.ErrorMessages;

public class AccountAlreadyClosedException extends RuntimeException{
    public AccountAlreadyClosedException() {
        super(ErrorMessages.ACCOUNT_ALREADY_CLOSED);
    }
}
