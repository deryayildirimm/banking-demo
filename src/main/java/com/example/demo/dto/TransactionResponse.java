package com.example.demo.dto;


import com.example.demo.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(

        Long transactionId,
        Long accountId,
        TransactionType type,
        BigDecimal amount,
        Instant createdAt,
        BigDecimal balanceAfter,
        String description
) {
}
