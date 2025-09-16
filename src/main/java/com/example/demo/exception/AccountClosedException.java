package com.example.demo.exception;

import com.example.demo.common.ErrorMessages;

public class AccountClosedException extends RuntimeException{

    public AccountClosedException() {
        super(ErrorMessages.ACCOUNT_CLOSED);
    }
}
