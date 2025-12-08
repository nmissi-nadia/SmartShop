package com.smart.shop.controller;

import com.smart.shop.config.RoleRequired;
import com.smart.shop.dto.Commande.CommandeCreateDto;
import com.smart.shop.dto.Commande.CommandeResponseDto;
import com.smart.shop.enums.StatutCommande;
import com.smart.shop.enums.UserRole;
import com.smart.shop.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    // -------------------------------------------------------
    // CRÉER UNE COMMANDE
    // -------------------------------------------------------
    @PostMapping
    @RoleRequired(UserRole.CLIENT)
    public ResponseEntity<CommandeResponseDto> creerCommande(
            @Valid @RequestBody CommandeCreateDto dto) {

        CommandeResponseDto response = commandeService.creerCommande(dto);
        return ResponseEntity.status(201).body(response);
    }

    // -------------------------------------------------------
    // OBTENIR UNE COMMANDE PAR ID
    // -------------------------------------------------------
    @GetMapping("/{id}")
    @RoleRequired({UserRole.ADMIN, UserRole.CLIENT})
    public ResponseEntity<CommandeResponseDto> obtenirCommande(@PathVariable String id) {

        return ResponseEntity.ok(commandeService.obtenirCommandeParId(id));
    }

    // -------------------------------------------------------
    // OBTENIR TOUTES LES COMMANDES D’UN CLIENT
    // -------------------------------------------------------
    @GetMapping("/client/{clientId}")
    @RoleRequired({UserRole.ADMIN, UserRole.CLIENT})
    public ResponseEntity<List<CommandeResponseDto>> obtenirCommandesParClient(
            @PathVariable String clientId) {

        return ResponseEntity.ok(commandeService.obtenirCommandesParClient(clientId));
    }

    // -------------------------------------------------------
    // RECHERCHER PAR STATUT
    // -------------------------------------------------------
    @GetMapping("/statut/{statut}")
    @RoleRequired(UserRole.ADMIN)
    public ResponseEntity<List<CommandeResponseDto>> rechercherParStatut(
            @PathVariable StatutCommande statut) {

        return ResponseEntity.ok(commandeService.rechercherCommandesParStatut(statut));
    }

    // -------------------------------------------------------
    // RECHERCHER PAR CLIENT + STATUT
    // -------------------------------------------------------
    @GetMapping("/recherche")
    @RoleRequired({UserRole.ADMIN, UserRole.CLIENT})
    public ResponseEntity<List<CommandeResponseDto>> rechercherParClientEtStatut(
            @RequestParam String clientId,
            @RequestParam StatutCommande statut) {

        return ResponseEntity.ok(
                commandeService.rechercherCommandesParClientEtStatut(clientId, statut)
        );
    }

    // -------------------------------------------------------
    // METTRE À JOUR LE STATUT D’UNE COMMANDE
    // -------------------------------------------------------
    @PutMapping("/{id}/statut")
    @RoleRequired(UserRole.ADMIN)
    public ResponseEntity<CommandeResponseDto> mettreAJourStatut(
            @PathVariable String id,
            @RequestParam StatutCommande statut) {

        return ResponseEntity.ok(
                commandeService.mettreAJourStatut(id, statut)
        );
    }
}
