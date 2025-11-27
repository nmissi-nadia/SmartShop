package com.smart.shop.mapper;

import com.smart.shop.config.MapStructConfig;
import com.smart.shop.dto.UserCreateDto;
import com.smart.shop.dto.UserCreateDto;
import com.smart.shop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(UserCreateDto dto);
    
    UserCreateDto toDto(User user);
}