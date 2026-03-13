package com.slique.categoryservice.service;

import com.slique.categoryservice.dto.SalonDto;
import com.slique.categoryservice.model.Category;

import java.util.Set;

public interface CategoryService {
	Category saveCategory(Category category, SalonDto salonDto);
	Category updateCategory(Long categoryId, Category category);
	Set<Category> getAllCategory(Long salonId);
	Category getCategoryById(Long categoryId);
	void deleteCategoryById(Long categoryId, Long salonId);
	
}
