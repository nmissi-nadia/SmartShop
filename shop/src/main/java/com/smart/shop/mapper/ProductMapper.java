package com.smart.shop.mapper;

import com.smart.shop.dto.Product.*;
import com.smart.shop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    Product toEntity(ProductCreateDto dto);
    ProductResponseDto toDto(Product product);
    void updateFromDto(ProductUpdateDto dto, @MappingTarget Product product);
}