package com.smart.shop.dto.Client;


import com.smart.shop.enums.CustomerTier;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ClientStatsDto {
    private long nombreCommandes;
    private BigDecimal montantTotalDepense;
    private LocalDateTime datePremiereCommande;
    private LocalDateTime dateDerniereCommande;
    private CustomerTier niveauFidelite;
}