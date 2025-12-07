package com.smart.shop.dto.Paiement;

import com.smart.shop.enums.TypePaiement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaiementCreateDto {
    @NotBlank(message = "L'ID de la commande est obligatoire")
    private String commandeId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    @NotNull(message = "Le type de paiement est obligatoire")
    private TypePaiement typePaiement;

    // Champs optionnels selon le type
    private String numeroCheque;
    private String nomBanque;
    private LocalDateTime dateEcheance;
    private String referenceVirement;
}
