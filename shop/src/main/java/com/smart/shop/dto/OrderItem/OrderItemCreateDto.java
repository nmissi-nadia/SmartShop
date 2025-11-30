package com.smart.shop.dto.OrderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderItemCreateDto {
    @NotBlank(message = "L'ID du produit est obligatoire")
    private String produitId;
    
    @Min(value = 1, message = "La quantité doit être supérieure à 0")
    private int quantite;
}
