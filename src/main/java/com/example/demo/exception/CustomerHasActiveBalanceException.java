package com.example.demo.exception;

public class CustomerHasActiveBalanceException extends RuntimeException {
    public CustomerHasActiveBalanceException(Long id) {
        super("Customer " + id + " has active balance");
    }
}
