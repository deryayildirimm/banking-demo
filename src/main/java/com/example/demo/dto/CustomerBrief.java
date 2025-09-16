package com.example.demo.dto;

public record CustomerBrief(
        Long id,
        String name,
        String phone,
        String email
) {
}
