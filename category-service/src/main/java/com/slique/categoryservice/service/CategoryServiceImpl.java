package com.slique.categoryservice.service;

import com.slique.categoryservice.dto.CategoryDto;
import com.slique.categoryservice.dto.SalonDto;
import com.slique.categoryservice.model.Category;
import com.slique.categoryservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
	private final CategoryRepository categoryRepository;
	
	
	@Override
	public Category saveCategory(CategoryDto categoryDto, SalonDto salonDto) {

		Category category = new Category();
		category.setName(categoryDto.getName());
		category.setImage(categoryDto.getImage());
		category.setSalonId(salonDto.getSalonId());
		return categoryRepository.save(category);

	}
	
	@Override
	public Category updateCategory(Long categoryId, Category category) {
		return null;
	}
	
	@Override
	public Set<Category> getAllCategoriesBySalon(Long salonId) {
		return categoryRepository.findBySalonId(salonId);
		
	}
	
	@Override
	public Category getCategoryById(Long categoryId) {
		Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
		if(categoryOptional.isPresent()){
			return categoryOptional.get();
		}
		throw new RuntimeException("Category not found");
	}
	
	@Override
	public void deleteCategoryById(Long categoryId, Long salonId) {
		Category category = getCategoryById(categoryId);
		if(! category.getSalonId().equals(salonId))
			throw new RuntimeException("Don't have permission to delete");
		categoryRepository.delete(category);
	}

	@Override
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}
}
