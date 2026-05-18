package com.slique.serviceoffering.service.client;

import com.slique.serviceoffering.dto.CategoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "CATEGORY", path = "/api/categories")
public interface CategoryFeignClient {

    @GetMapping("/{categoryId}")
    ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long categoryId);

}
