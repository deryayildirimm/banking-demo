package com.example.demo.dto;

public record TransferResponse(
        TransactionResponse in,
        TransactionResponse out
) {
}
