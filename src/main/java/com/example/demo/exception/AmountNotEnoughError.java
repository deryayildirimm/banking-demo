package com.example.demo.exception;

import com.example.demo.common.ErrorMessages;

public class AmountNotEnoughError extends RuntimeException {

    public AmountNotEnoughError() {
        super(ErrorMessages.AMOUNT_CANNOT_BE_NEGATIVE);
    }
}
