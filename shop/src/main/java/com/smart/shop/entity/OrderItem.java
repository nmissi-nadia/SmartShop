package com.smart.shop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Product produit;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire = BigDecimal.ZERO;

    @Column(name = "total_ligne", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalLigne = BigDecimal.ZERO;

    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        calculerTotalLigne();
    }

    @PreUpdate
    public void calculerTotalLigne() {
        if (prixUnitaire == null) {
            prixUnitaire = BigDecimal.ZERO;
        }
        if (quantite <= 0) {
            quantite = 1;
        }
        this.totalLigne = prixUnitaire.multiply(new BigDecimal(quantite))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire != null ? prixUnitaire : BigDecimal.ZERO;
    }
}