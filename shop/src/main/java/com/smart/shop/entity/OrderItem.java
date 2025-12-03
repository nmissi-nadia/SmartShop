package com.smart.shop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
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
    private BigDecimal prixUnitaire;

    @Column(name = "total_ligne", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalLigne;

    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        calculerTotalLigne();
    }

    @PreUpdate
    public void calculerTotalLigne() {
        if (this.prixUnitaire != null && this.quantite != null) {
            this.totalLigne = this.prixUnitaire.multiply(BigDecimal.valueOf(this.quantite));
        }
    }
}