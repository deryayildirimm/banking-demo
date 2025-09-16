package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(

  @NotBlank Long fromAccountId,
  @NotBlank  Long toAccountId,
  @Positive BigDecimal amount,
    String description   ) { }
