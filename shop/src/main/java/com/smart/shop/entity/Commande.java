package com.smart.shop.entity;

import com.smart.shop.enums.StatutCommande;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "commandes")
public class Commande {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "user_id", nullable = false)
    private Client client;
    
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    @Column(name = "date_commande", nullable = false)
    private LocalDateTime dateCommande;
    
    @Column(name = "sous_total", precision = 10, scale = 2)
    private BigDecimal sousTotal= BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal remise = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal tva= BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal total= BigDecimal.ZERO;
    
    @Column(name = "code_promo")
    private String codePromo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut = StatutCommande.PENDING;
    
    @Column(name = "montant_restant", precision = 10, scale = 2)
    private BigDecimal montantRestant= BigDecimal.ZERO;

    public Commande(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être nul");
        }
        this.client = client;
    }
    
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

    public void ajouterItem(OrderItem item) {
        this.items.add(item);
        item.setCommande(this);
    }

    public void calculerTotaux() {
        this.sousTotal = items.stream()
                .map(OrderItem::getTotalLigne)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantApresRemise = this.sousTotal.subtract(this.remise).max(BigDecimal.ZERO);

        // 3. Calculer la TVA sur le montant après remise.
        this.tva = montantApresRemise.multiply(new BigDecimal("0.20"))
                .setScale(2, RoundingMode.HALF_UP);

        // 4. Calculer le total final et mettre à jour le montant restant.
        this.total = montantApresRemise.add(tva);
        this.montantRestant = this.total;
    }
    public void setRemise(BigDecimal remise) {
        this.remise = remise != null ? remise : BigDecimal.ZERO;
    }
}