package com.smart.shop.entity;

import com.smart.shop.enums.StatutCommande;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "commandes")
public class Commande {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    @Column(name = "date_commande", nullable = false)
    private LocalDateTime dateCommande;
    
    @Column(name = "sous_total", precision = 10, scale = 2)
    private BigDecimal sousTotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal remise = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal tva;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "code_promo")
    private String codePromo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut = StatutCommande.PENDING;
    
    @Column(name = "montant_restant", precision = 10, scale = 2)
    private BigDecimal montantRestant;
    
    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        this.dateCommande = LocalDateTime.now();
        calculerTotaux();
    }
    
    @PreUpdate
    protected void onUpdate() {
        calculerTotaux();
    }

    public void calculerTotaux() {
        if (items != null) {
            this.sousTotal = items.stream()
                .map(OrderItem::getTotalLigne)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calcul du total après remise et TVA
            BigDecimal totalAvantTva = sousTotal.subtract(remise != null ? remise : BigDecimal.ZERO);
            this.tva = totalAvantTva.multiply(new BigDecimal("0.20")); // 20% de TVA
            this.total = totalAvantTva.add(tva);

            // Calcul du montant restant
            if (montantRestant == null) {
                this.montantRestant = total;
            }
        }
    }
}