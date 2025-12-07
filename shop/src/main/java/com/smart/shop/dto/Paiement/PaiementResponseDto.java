package com.smart.shop.dto.Paiement;

import com.smart.shop.enums.PaymentStatus;
import com.smart.shop.enums.TypePaiement;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaiementResponseDto {
    private String id;
    private String commandeId;
    private BigDecimal montant;
    private TypePaiement typePaiement;
    private PaymentStatus statut;
    private LocalDateTime datePaiement;
    private LocalDateTime dateEncaissement;
    private String numeroCheque;
    private String nomBanque;
    private LocalDateTime dateEcheance;
    private String referenceVirement;
}
