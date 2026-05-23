package com.slique.categoryservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {

    private Long id;
    private String name;
    private String image;
    private Long salonId;


}
