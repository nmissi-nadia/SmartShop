package com.smart.shop.dto.Client;


import java.math.BigDecimal;
import java.time.LocalDate;

public class ClientStatsDto {
    private int nombreCommandes;
    private BigDecimal montantTotalDepense;
    private LocalDate premiereCommande;
    private LocalDate derniereCommande;
}