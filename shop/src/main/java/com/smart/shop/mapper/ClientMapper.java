package com.smart.shop.mapper;

import com.smart.shop.config.MapStructConfig;
import com.smart.shop.dto.Client.ClientCreateDto;
import com.smart.shop.dto.Client.ClientResponseDto;
import com.smart.shop.dto.Client.ClientUpdateDto;
import com.smart.shop.entity.Client;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapStructConfig.class, uses = UserMapper.class)
public interface ClientMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Client toEntity(ClientCreateDto dto);
    
    ClientResponseDto toDto(Client client);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(ClientUpdateDto dto, @MappingTarget Client entity);
}