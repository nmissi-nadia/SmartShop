package com.smart.shop.mapper;

import com.smart.shop.dto.Client.*;
import com.smart.shop.entity.Client;
import com.smart.shop.entity.Commande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CommandeMapper.class})
public abstract class ClientMapper {


    @Mapping(target = "niveauFidelite", source = "niveauFidelite")
    public abstract ClientMinimalDto toMinimalDto(Client client);
    
    @Mapping(target = "id", ignore = true)
    public abstract Client toEntity(ClientCreateDto dto);
    
    @Mapping(target = "id", ignore = true)
    public abstract void updateFromDto(ClientUpdateDto dto, @MappingTarget Client client);

    public abstract ClientResponseDto toResponseDto(Client client);

    // Méthode pour mapper une Commande vers le DTO de l'historique
    public abstract ClientOrderHistoryDto commandeToClientOrderHistoryDto(Commande commande);

    public abstract List<ClientOrderHistoryDto> commandesToClientOrderHistoryDtos(List<Commande> commandes);
}