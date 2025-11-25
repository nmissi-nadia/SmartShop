package com.smart.shop.entity;

import com.smart.shop.enums.CustomerTier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @SuperBuilder
@Table(name = "clients")
public class Client extends User{
    private String nom;
    private String email;

    @Enumerated(EnumType.STRING)
    private CustomerTier niveauFidelite;
}
