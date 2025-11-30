package com.smart.shop.dto.OrderItem;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponseDto {
    private Long id;
    private String produitId;
    private String nomProduit;
    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal totalLigne;
}