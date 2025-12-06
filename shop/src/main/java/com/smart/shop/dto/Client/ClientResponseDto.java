package com.smart.shop.dto.Client;



import com.smart.shop.enums.CustomerTier;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDto {
    private String id;
    private String nom;
    private String email;
    private CustomerTier niveauFidelite; // Correspond à Client.niveauFidelite
    private long nombreCommandes; // Correspond à Client.nombreCommandes
    private BigDecimal montantTotalDepense; // Correspond à Client.montantTotalDepense
    private LocalDateTime datePremiereCommande; // Correspond à Client.datePremiereCommande
    private LocalDateTime dateDerniereCommande; // Correspond à Client.dateDerniereCommande
    private List<ClientOrderHistoryDto> commandes; // Historique des commandes
}