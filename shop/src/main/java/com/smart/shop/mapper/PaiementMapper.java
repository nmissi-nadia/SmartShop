package com.smart.shop.mapper;

import com.smart.shop.dto.Paiement.PaiementCreateDto;
import com.smart.shop.dto.Paiement.PaiementResponseDto;
import com.smart.shop.entity.Paiement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaiementMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commande", ignore = true) // Géré manuellement dans le service
    Paiement toEntity(PaiementCreateDto dto);

    @Mapping(source = "commande.id", target = "commandeId")
    PaiementResponseDto toDto(Paiement paiement);
}
