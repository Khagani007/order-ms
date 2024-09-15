package com.example.orderms.client;

import com.example.orderms.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-ms", url = "http://localhost:8081")
public interface CustomerClient {
    @GetMapping("/api/v1/customers/{id}")
    CustomerDto getCustomerById(@PathVariable("id") Long id);
}
