package com.smart.shop.entity;

import com.smart.shop.enums.TypePaiement;
import com.smart.shop.enums.PaymentStatus;
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
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;
    @Column(name = "numero_paiement", nullable = false)
    private Integer numeroPaiement;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_paiement", nullable = false)
    private TypePaiement typePaiement;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", nullable = false)
    private PaymentStatus statut = PaymentStatus.EN_ATTENTE;
    
    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement;
    
    @Column(name = "date_encaissement")
    private LocalDateTime dateEncaissement;

    // --- Champs spécifiques pour les chèques ---
    @Column(name = "cheque_numero")
    private String numeroCheque;

    @Column(name = "cheque_banque")
    private String nomBanque;

    @Column(name = "cheque_date_echeance")
    private LocalDateTime dateEcheance;

    // --- Champ spécifique pour les virements ---
    @Column(name = "virement_reference")
    private String referenceVirement;
    
    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        if (this.datePaiement == null) {
            this.datePaiement = LocalDateTime.now();
        }
    }
}