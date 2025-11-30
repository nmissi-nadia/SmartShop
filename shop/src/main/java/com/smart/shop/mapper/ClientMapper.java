package com.smart.shop.mapper;

import com.smart.shop.config.MapStructConfig;
import com.smart.shop.dto.Client.*;
import com.smart.shop.entity.Client;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    
    @Mapping(target = "niveauFidelite", source = "niveauFidelite")
    ClientMinimalDto toMinimalDto(Client client);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commandes", ignore = true)
    Client toEntity(ClientCreateDto dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commandes", ignore = true)
    void updateFromDto(ClientUpdateDto dto, @MappingTarget Client client);

    ClientResponseDto toDto(Client save);
}