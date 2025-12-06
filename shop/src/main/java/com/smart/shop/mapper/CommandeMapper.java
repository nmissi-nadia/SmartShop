package com.smart.shop.mapper;

import com.smart.shop.dto.Client.ClientMinimalDto;
import com.smart.shop.dto.Commande.CommandeCreateDto;
import com.smart.shop.dto.Commande.CommandeResponseDto;
import com.smart.shop.dto.Commande.CommandeUpdateDto;
import com.smart.shop.entity.Client;
import com.smart.shop.entity.Commande;
import com.smart.shop.entity.OrderItem;
import com.smart.shop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, ClientMapper.class})
public abstract class CommandeMapper {

    @Mapping(target = "lignes", source = "items")
    public abstract CommandeResponseDto toDto(Commande commande);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true) // Le client est défini manuellement dans le service
    @Mapping(target = "items", source = "items")
    public abstract Commande toEntity(CommandeCreateDto dto);
    
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "items", ignore = true)
    public abstract void updateFromDto(CommandeUpdateDto dto, @MappingTarget Commande commande);
}