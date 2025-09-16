package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AccountRequest(

      @Positive BigDecimal balance,
      @NotNull  Long customerId
) {
}
