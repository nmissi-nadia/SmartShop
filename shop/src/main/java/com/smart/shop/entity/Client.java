package com.smart.shop.entity;

import com.smart.shop.enums.CustomerTier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @SuperBuilder
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "user_id")
public class Client extends User{
    @Id
    private String id;
    private String nom;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_fidelite")
    private CustomerTier niveauFidelite;

    
}
