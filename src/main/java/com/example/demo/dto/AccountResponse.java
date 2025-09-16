package com.example.demo.dto;


import java.math.BigDecimal;

import java.util.List;

public record AccountResponse(
        Long id,
        BigDecimal balance,
        String createdAt,
        CustomerBrief customerBrief,
        Long transactionCount,
        List<TransactionBrief> transactionBriefList

) {

    // Kolaylık için aşırı-yüklenmiş ctor (transactions parametresi yok → otomatik [])
    public AccountResponse(Long id, BigDecimal balance, String createdAt, CustomerBrief customer , Long transactionCount) {
        this(id, balance, createdAt, customer, transactionCount,  List.of());
    }

}
