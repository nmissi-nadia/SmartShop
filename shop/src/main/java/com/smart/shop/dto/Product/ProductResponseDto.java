package com.smart.shop.dto.Product;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponseDto {
    private String id;
    private String nom;
    private BigDecimal prixUnitaire;
    private int stockDisponible;
}