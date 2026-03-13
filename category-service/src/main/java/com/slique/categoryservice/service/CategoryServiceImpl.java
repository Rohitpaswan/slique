package com.slique.categoryservice.service;

import com.slique.categoryservice.dto.SalonDto;
import com.slique.categoryservice.model.Category;
import com.slique.categoryservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
	private final CategoryRepository categoryRepository;
	
	
	@Override
	public Category saveCategory(Category category, SalonDto salonDto) {
	
		Category savedCategory = Category.builder()
				.name(category.getName())
				.image(category.getImage())
				.salonId(salonDto.getSalonId())
				.build();
		return categoryRepository.save(savedCategory);
	}
	
	@Override
	public Category updateCategory(Long categoryId, Category category) {
		return null;
	}
	
	@Override
	public Set<Category> getAllCategory(Long salonId) {
		return categoryRepository.findBySalonId(salonId);
		
	}
	
	@Override
	public Category getCategoryById(Long categoryId) {
		Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
		if(categoryOptional.isPresent()){
			return categoryOptional.get();
		}
		throw new RuntimeException("not find category");
	}
	
	@Override
	public void deleteCategoryById(Long categoryId, Long salonId) {
		Category category = getCategoryById(categoryId);
		if(! category.getSalonId().equals(salonId))
			throw new RuntimeException("Dont have permission to delete");
		categoryRepository.delete(category);
	}
}
