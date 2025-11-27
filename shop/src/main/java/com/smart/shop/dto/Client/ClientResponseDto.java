package com.smart.shop.dto.Client;


import com.smart.shop.enums.CustomerTier;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClientResponseDto {
    private String id;
    private String nom;
    private String email;
    private CustomerTier niveauFidelite;
    private Long nombreTotalCommandes;
    private Double montantCumuleCommandes;
    private LocalDate premiereCommande;
    private LocalDate derniereCommande;
//    private List<CommandeDto> historiqueCommandes;
}