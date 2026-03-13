package com.slique.categoryservice.controller;

import com.slique.categoryservice.model.Category;
import com.slique.categoryservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	public final CategoryService categoryService;
	
	
	@GetMapping("/salon/{salonId}")
	public ResponseEntity<Set<Category>> getAllCategory(@PathVariable Long salonId){
		return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategory(salonId));
	}
	
	@GetMapping("/{categoryId}")
	public ResponseEntity<Category> getCategoryById(
			@PathVariable Long categoryId) {
		
		Category category = categoryService.getCategoryById(categoryId);
		return ResponseEntity.ok(category);
	}
}
