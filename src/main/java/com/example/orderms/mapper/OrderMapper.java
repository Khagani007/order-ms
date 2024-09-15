package com.example.orderms.mapper;

import com.example.orderms.dto.OrderDto;
import com.example.orderms.entity.OrderEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderDto toDto(OrderEntity order) {
        return OrderDto.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .productId(order.getProductId())
                .price(order.getPrice())
                .count(order.getCount())
                .build();
    }

    public OrderEntity toEntity(OrderDto orderDto) {
        return OrderEntity.builder()
                .customerId(orderDto.getCustomerId())
                .productId(orderDto.getProductId())
                .price(orderDto.getPrice())
                .count(orderDto.getCount())
                .build();
    }
}
