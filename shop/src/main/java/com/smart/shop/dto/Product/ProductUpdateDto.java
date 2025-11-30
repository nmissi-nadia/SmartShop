package com.smart.shop.dto.Product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductUpdateDto {
    private String nom;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal prixUnitaire;
    
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer stockDisponible;
}
