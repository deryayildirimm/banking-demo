package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest(
       @NotNull Long accountId,
       @Positive @NotNull BigDecimal amount,
       @NotBlank String desc
) {
}
