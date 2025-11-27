package com.smart.shop.entity;

import com.smart.shop.enums.TypePaiement;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "paiements")
public class Paiement {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;
    
    @Column(name = "numero_paiement", nullable = false)
    private Integer numeroPaiement;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_paiement", nullable = false)
    private TypePaiement typePaiement;
    
    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement;
    
    @Column(name = "date_encaissement")
    private LocalDateTime dateEncaissement;
    
    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        if (this.datePaiement == null) {
            this.datePaiement = LocalDateTime.now();
        }
    }
}