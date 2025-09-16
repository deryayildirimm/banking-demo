package com.example.demo.exception;

import com.example.demo.common.ErrorMessages;

public class SameAccountTransferException extends RuntimeException {
    public SameAccountTransferException(Long id) {
        super(ErrorMessages.SAME_ACCOUNT_TRANSFER + id);
    }
}
