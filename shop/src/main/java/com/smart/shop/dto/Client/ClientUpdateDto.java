package com.smart.shop.dto.Client;

import com.smart.shop.enums.CustomerTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientUpdateDto {
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String nom;
    
    @Email(message = "L'email doit être valide")
    private String email;
    
    private CustomerTier niveauFidelite;
    
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
}