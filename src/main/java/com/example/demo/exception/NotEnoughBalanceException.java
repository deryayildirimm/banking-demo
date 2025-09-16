package com.example.demo.exception;

import com.example.demo.common.ErrorMessages;

public class NotEnoughBalanceException extends RuntimeException {
    public NotEnoughBalanceException() {
        super(ErrorMessages.BALANCE_NOT_ENOUGH);
    }
}
