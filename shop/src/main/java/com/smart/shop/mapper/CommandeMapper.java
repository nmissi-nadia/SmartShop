package com.smart.shop.mapper;

import com.smart.shop.dto.Client.ClientMinimalDto;
import com.smart.shop.dto.Commande.CommandeCreateDto;
import com.smart.shop.dto.Commande.CommandeResponseDto;
import com.smart.shop.dto.Commande.CommandeUpdateDto;
import com.smart.shop.entity.Commande;
import com.smart.shop.entity.OrderItem;
import com.smart.shop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ClientMapper.class})
public interface CommandeMapper {
    
    @Mapping(target = "client", source = "commande.client")
    CommandeResponseDto toDto(Commande commande);
    
    @Mapping(target = "client", ignore = true)
    Commande toEntity(CommandeCreateDto dto);
    
    @Mapping(target = "client", ignore = true)
    void updateFromDto(CommandeUpdateDto dto, @MappingTarget Commande commande);
    
    // Méthode pour mapper une liste de OrderItem
    default List<OrderItem> toOrderItems(Set<CommandeCreateDto.LigneCommandeDto> lignes) {
        if (lignes == null || lignes.isEmpty()) {
            return Collections.emptyList();
        }
        return lignes.stream()
                .map(this::toOrderItem)
                .collect(Collectors.toList());
    }
    
    // Méthode pour mapper un OrderItem
    default OrderItem toOrderItem(CommandeCreateDto.LigneCommandeDto ligne) {
        if (ligne == null) {
            return null;
        }
        
        OrderItem item = new OrderItem();
        Product produit = new Product();
        produit.setId(ligne.getProduitId());
        item.setProduit(produit);
        item.setQuantite(ligne.getQuantite());
        item.calculerTotalLigne();
        return item;
    }
}