package com.smart.shop.mapper;

import com.smart.shop.dto.UserCreateDTO;
import com.smart.shop.dto.UserResponseDTO;
import com.smart.shop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(UserCreateDTO dto);

    UserResponseDTO toDto(User user);
}