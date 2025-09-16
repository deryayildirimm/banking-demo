package com.example.demo.exception;

import java.util.function.Supplier;

public class ThrowExceptionHandler {

    public static void throwIf(boolean condition, Supplier<RuntimeException> exceptionSupplier) {
        if (condition) {
            throw exceptionSupplier.get();
        }
    }
}
