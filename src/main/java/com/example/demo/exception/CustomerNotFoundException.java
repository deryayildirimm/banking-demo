package com.example.demo.exception;

import com.example.demo.common.ErrorMessages;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException(Long id) {
        super(ErrorMessages.CUSTOMER_NOT_EXISTS + id);
    }
}
