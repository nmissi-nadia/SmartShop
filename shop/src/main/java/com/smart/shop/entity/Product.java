package com.smart.shop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;
    
    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible;
    
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean actif = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}