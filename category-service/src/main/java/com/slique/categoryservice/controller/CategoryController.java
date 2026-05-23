package com.slique.categoryservice.controller;

import com.slique.categoryservice.dto.SalonDto;

import com.slique.categoryservice.model.Category;
import com.slique.categoryservice.service.CategoryService;
import com.slique.categoryservice.service.client.SalonFeignClient;
import com.slique.categoryservice.service.client.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	private final CategoryService categoryService;
	private final UserFeignClient userFeignClient;
	private final SalonFeignClient salonFeignClient;

	// Get all Categories
	@GetMapping
	public ResponseEntity<List<Category>> getAllCategories() {
		List<Category> categories = categoryService.getAllCategories();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@GetMapping("/salon/{salonId}")
	public ResponseEntity<Set<Category>> getAllCategoriesBySalon(@PathVariable Long salonId, @RequestHeader("Authorization") String jwt){
		// Verify the user token is valid before proceeding
		userFeignClient.getUserFromJwtToken(jwt);
		SalonDto salon=validateSalonOwnership(jwt, salonId);

		Set<Category> categories = categoryService
				.getAllCategoriesBySalon(salon.getSalonId());
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}
	
	@GetMapping("/{categoryId}")
	public ResponseEntity<Category> getCategoryById(
			@PathVariable Long categoryId) {
		
		Category category = categoryService.getCategoryById(categoryId);
		return ResponseEntity.ok(category);
	}

	private SalonDto validateSalonOwnership(String jwt, Long salonId) {
		List<SalonDto> salonDtos = salonFeignClient.getSalonBYOwnerId(jwt).getBody();

		if (salonDtos == null || salonDtos.isEmpty()) {
			throw new RuntimeException("No salons found for this owner");
		}
		return salonDtos.stream().filter(salon -> salon.getSalonId().equals(salonId)).findFirst().orElseThrow(() -> new RuntimeException("Salon not found or unauthorized"));
	}
}
