package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountBrief(

        BigDecimal balance,
        Instant createdAt,
        CustomerBrief customer

) {
}
