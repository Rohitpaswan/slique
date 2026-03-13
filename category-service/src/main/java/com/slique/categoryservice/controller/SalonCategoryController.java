package com.slique.categoryservice.controller;

import com.slique.categoryservice.dto.SalonDto;
import com.slique.categoryservice.model.Category;
import com.slique.categoryservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories/salon-owner")
@RequiredArgsConstructor
public class SalonCategoryController {
	public final CategoryService categoryService;
	@PostMapping()
	public ResponseEntity<Category> createCategory(@RequestBody Category category){
		SalonDto salonDto = new SalonDto();
		salonDto.setSalonId(1L);
		return  ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(category, salonDto));
	}
	
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId){
		SalonDto salonDto = new SalonDto();
		salonDto.setSalonId(1L);
		categoryService.deleteCategoryById(categoryId, salonDto.getSalonId());
		return ResponseEntity.ok("Delete sucfful");
	}
}
