package com.smart.shop.dto.Commande;

import com.smart.shop.dto.Client.ClientMinimalDto;
import com.smart.shop.enums.StatutCommande;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommandeResponseDto {
    private String id;
    private ClientMinimalDto client;
    private List<LigneCommandeResponseDto> lignes;
    private LocalDateTime dateCommande;
    private BigDecimal sousTotal;
    private BigDecimal remise;
    private BigDecimal tva;
    private BigDecimal total;
    private String codePromo;
    private StatutCommande statut;
    private BigDecimal montantRestant;
    
    @Data
    public static class LigneCommandeResponseDto {
        private String produitId;
        private String nomProduit;
        private int quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal totalLigne;
    }
}