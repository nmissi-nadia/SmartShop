package com.smart.shop.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.smart.shop.enums.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @SuperBuilder
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "id")
public class Client extends User{
    private String nom;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_fidelite")
    private CustomerTier niveauFidelite;

    @Column(name = "nombre_commandes")
    private long nombreCommandes = 0L;

    @Column(name = "montant_total_depense", precision = 10, scale = 2)
    private java.math.BigDecimal montantTotalDepense = java.math.BigDecimal.ZERO;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Commande> commandes = new ArrayList<>();

    @Column(name = "date_premiere_commande")
    private LocalDateTime datePremiereCommande;

    @Column(name = "date_derniere_commande")
    private LocalDateTime dateDerniereCommande;

}
