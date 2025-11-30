package com.smart.shop.dto.Commande;

import com.smart.shop.enums.StatutCommande;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommandeUpdateDto {
    @NotNull(message = "Le statut de la commande est obligatoire")
    private StatutCommande statut;
    
    private String codePromo;
}