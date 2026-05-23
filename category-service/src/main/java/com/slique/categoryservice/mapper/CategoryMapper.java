package com.slique.categoryservice.mapper;

import com.slique.categoryservice.dto.CategoryDto;
import com.slique.categoryservice.model.Category;

public class CategoryMapper {
    private CategoryMapper(){}

    public static CategoryDto mapToCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .image(category.getImage())
                .salonId(category.getSalonId()) // Included salonId from our previous step
                .build();
    }

}
