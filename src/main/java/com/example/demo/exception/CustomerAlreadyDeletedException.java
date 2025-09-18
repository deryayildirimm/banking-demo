package com.example.demo.exception;

import com.example.demo.common.ErrorMessages;

public class CustomerAlreadyDeletedException extends RuntimeException {
    public CustomerAlreadyDeletedException() {
        super(ErrorMessages.CUSTOMER_HAS_BEEN_DELETED);
    }
}
