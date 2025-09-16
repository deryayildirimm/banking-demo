package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
       @NotNull Long id,
       @NotBlank String name,
        String address,
        String phone,
       @Email @NotBlank String email

) {
}
