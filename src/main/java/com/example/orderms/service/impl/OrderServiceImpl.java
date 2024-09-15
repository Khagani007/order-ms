package com.example.orderms.service.impl;

import com.example.orderms.client.CustomerClient;
import com.example.orderms.client.ProductClient;
import com.example.orderms.dto.OrderDto;
import com.example.orderms.dto.ProductDto;
import com.example.orderms.entity.OrderEntity;
import com.example.orderms.exception.InsufficientStockException;
import com.example.orderms.exception.ResourceNotFoundException;
import com.example.orderms.mapper.OrderMapper;
import com.example.orderms.repository.OrderRepository;
import com.example.orderms.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper,
                            CustomerClient customerClient, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.customerClient = customerClient;
        this.productClient = productClient;
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        customerClient.getCustomerById(orderDto.getCustomerId());

        ProductDto product = productClient.getProductById(orderDto.getProductId());
        if (product.getCount() < orderDto.getCount()) {
            throw new InsufficientStockException("Insufficient stock for product with id: " + orderDto.getProductId());
        }

        OrderEntity orderEntity = orderMapper.toEntity(orderDto);
        OrderEntity savedOrder = orderRepository.save(orderEntity);

        product.setCount(product.getCount() - orderDto.getCount());
        productClient.updateProduct(product.getId(), product);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    public OrderDto getOrderById(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        OrderEntity existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        ProductDto product = productClient.getProductById(orderDto.getProductId());
        if (product.getCount() < orderDto.getCount()) {
            throw new InsufficientStockException("Insufficient stock for product with id: " + orderDto.getProductId());
        }

        if (!existingOrder.getProductId().equals(orderDto.getProductId())) {
            ProductDto oldProduct = productClient.getProductById(existingOrder.getProductId());
            oldProduct.setCount(oldProduct.getCount() + existingOrder.getCount());
            productClient.updateProduct(oldProduct.getId(), oldProduct);

            product.setCount(product.getCount() - orderDto.getCount());
            productClient.updateProduct(product.getId(), product);
        } else {
            int oldCount = existingOrder.getCount();
            int newCount = orderDto.getCount();
            if (newCount > oldCount) {
                int additionalCount = newCount - oldCount;
                if (product.getCount() < additionalCount) {
                    throw new InsufficientStockException("Insufficient stock for product with id: " + orderDto.getProductId());
                }
                product.setCount(product.getCount() - additionalCount);
            } else {
                product.setCount(product.getCount() + (oldCount - newCount));
            }
            productClient.updateProduct(product.getId(), product);
        }

        existingOrder.setCustomerId(orderDto.getCustomerId());
        existingOrder.setProductId(orderDto.getProductId());
        existingOrder.setPrice(orderDto.getPrice());
        existingOrder.setCount(orderDto.getCount());

        OrderEntity updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toDto(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        ProductDto product = productClient.getProductById(orderEntity.getProductId());
        product.setCount(product.getCount() + orderEntity.getCount());
        productClient.updateProduct(product.getId(), product);

        orderRepository.delete(orderEntity);
    }

}
