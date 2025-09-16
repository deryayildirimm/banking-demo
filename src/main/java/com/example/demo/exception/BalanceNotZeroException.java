package com.example.demo.exception;

import com.example.demo.common.ErrorMessages;

import java.math.BigDecimal;

public class BalanceNotZeroException extends RuntimeException{
    public BalanceNotZeroException(BigDecimal balance) {
        super(ErrorMessages.BALANCE_NOT_ZERO +  balance );
    }
}
