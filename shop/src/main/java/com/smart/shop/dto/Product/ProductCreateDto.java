package com.smart.shop.dto.Product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductCreateDto {
    @NotBlank(message = "Le nom du produit est obligatoire")
    private String nom;
    
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal prixUnitaire;
    
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private int stockDisponible;
}