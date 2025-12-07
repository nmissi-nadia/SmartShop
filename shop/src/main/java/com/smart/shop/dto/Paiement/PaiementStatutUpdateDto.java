package com.smart.shop.dto.Paiement;

import com.smart.shop.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaiementStatutUpdateDto {
    @NotNull(message = "Le nouveau statut est obligatoire")
    private PaymentStatus statut;
}
