package com.smart.shop.dto.Commande;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class CommandeCreateDto {
    @NotNull(message = "L'ID du client est obligatoire")
    private String clientId;
    
    @NotEmpty(message = "La commande doit contenir au moins un article")
    private List<@Valid LigneCommandeDto> items; // Renommé pour correspondre à l'entité
    
    private String codePromo;
    
    @Data
    public static class LigneCommandeDto {
        @NotNull(message = "L'ID du produit est obligatoire")
        private String produitId;
        
        @Positive(message = "La quantité doit être supérieure à 0")
        private int quantite;
    }
}