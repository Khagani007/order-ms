package com.example.orderms.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerDto {
    private Long id;
    private String fullName;
    private int age;
    private String pin;
    private BigDecimal balance;
}
