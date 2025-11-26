package com.smart.shop.dto;

import com.smart.shop.enums.CustomerTier;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto extends UserDto {
    private String nom;
    private String email;
    private CustomerTier niveauFidelite;
}
