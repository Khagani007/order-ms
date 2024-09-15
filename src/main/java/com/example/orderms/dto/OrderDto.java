package com.example.orderms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDto {
    private Long id;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be a positive number")
    private double price;

    @NotNull(message = "Count is required")
    @Min(value = 0, message = "Count must be a positive number")
    private int count;
}
