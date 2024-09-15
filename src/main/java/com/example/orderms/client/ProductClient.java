package com.example.orderms.client;

import com.example.orderms.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-ms", url = "http://localhost:8082")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);

    @PutMapping("/api/products/{id}")
    ProductDto updateProduct(@PathVariable("id") Long id, @RequestBody ProductDto productDto);
}
