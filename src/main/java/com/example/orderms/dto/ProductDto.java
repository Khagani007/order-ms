package com.example.orderms.dto;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private double price;
    private int count;
}
