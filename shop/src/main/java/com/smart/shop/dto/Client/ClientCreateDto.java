package com.smart.shop.dto.Client;

import com.smart.shop.dto.UserCreateDto;
import com.smart.shop.enums.CustomerTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientCreateDto extends UserCreateDto {
    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotNull(message = "Le niveau de fidélité est requis")
    private CustomerTier niveauFidelite;
}