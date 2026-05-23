package com.slique.categoryservice.controller;

import com.slique.categoryservice.dto.CategoryDto;
import com.slique.categoryservice.dto.SalonDto;
import com.slique.categoryservice.mapper.CategoryMapper;
import com.slique.categoryservice.service.CategoryService;
import com.slique.categoryservice.service.client.SalonFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories/salon-owner")
@RequiredArgsConstructor
public class SalonCategoryController {
    public final CategoryService categoryService;
    private final SalonFeignClient salonFeignClient;

    @PostMapping()
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto, @RequestHeader("Authorization") String jwt) {

        SalonDto salonDto = validateSalonOwnership(jwt, categoryDto.getSalonId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryMapper.mapToCategoryDto(categoryService.saveCategory(categoryDto, salonDto)));
    }

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Void> deleteCategory(
			@PathVariable Long categoryId,
			@RequestHeader("Authorization") String jwt,
			@RequestParam Long salonId) {

		SalonDto salonDto = validateSalonOwnership(jwt, salonId);
		categoryService.deleteCategoryById(categoryId, salonDto.getSalonId());
		return ResponseEntity.noContent().build();
	}



    private SalonDto validateSalonOwnership(String jwt, Long salonId) {
        List<SalonDto> salonDtos = salonFeignClient.getSalonBYOwnerId(jwt).getBody();

        if (salonDtos == null || salonDtos.isEmpty()) {
            throw new RuntimeException("No salons found for this owner");
        }
        return salonDtos.stream().filter(salon -> salon.getSalonId().equals(salonId)).findFirst().orElseThrow(() -> new RuntimeException("Salon not found or unauthorized"));
    }
}
