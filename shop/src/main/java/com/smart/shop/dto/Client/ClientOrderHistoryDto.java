package com.smart.shop.dto.Client;

import com.smart.shop.enums.StatutCommande;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientOrderHistoryDto {
    private String id;
    private LocalDateTime dateCommande;
    private BigDecimal total;
    private StatutCommande statut;
}
