package com.example.demo.dto;

public record CustomerResponse(

        Long id,
        String name,
        String address,
        String phone,
        String email,
        Long accountTotal

) { }
