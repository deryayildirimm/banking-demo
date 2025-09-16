package com.example.demo.dto;

import com.example.demo.model.TransactionType;

import java.math.BigDecimal;

public record TransactionBrief(
        Long id,
        TransactionType type,
        BigDecimal amount,
        String createdAt

) {
}
