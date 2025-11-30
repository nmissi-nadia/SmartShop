package com.smart.shop.dto.Client;

import lombok.Data;

@Data
public class ClientMinimalDto {
    private String id;
    private String nom;
    private String email;
    private String niveauFidelite;
}