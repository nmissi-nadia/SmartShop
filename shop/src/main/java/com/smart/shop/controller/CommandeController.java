package com.smart.shop.controller;

import com.smart.shop.dto.Commande.CommandeCreateDto;
import com.smart.shop.dto.Commande.CommandeResponseDto;
import com.smart.shop.enums.StatutCommande;
import com.smart.shop.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {
    
    private final CommandeService commandeService;
    
    @PostMapping
    public ResponseEntity<CommandeResponseDto> creerCommande(@Valid @RequestBody CommandeCreateDto dto) {
        return new ResponseEntity<>(commandeService.creerCommande(dto), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CommandeResponseDto> obtenirCommande(@PathVariable String id) {
        return ResponseEntity.ok(commandeService.obtenirCommandeParId(id));
    }
    
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CommandeResponseDto>> obtenirCommandesClient(@PathVariable String clientId) {
        return ResponseEntity.ok(commandeService.obtenirCommandesParClient(clientId));
    }
    
    @PutMapping("/{id}/statut")
    public ResponseEntity<Void> mettreAJourStatut(
            @PathVariable String id,
            @RequestParam StatutCommande statut) {
        
        commandeService.mettreAJourStatut(id, statut);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<List<CommandeResponseDto>> rechercherCommandes(
            @RequestParam(required = false) String clientId,
            @RequestParam(required = false) StatutCommande statut) {
        
        if (clientId != null && statut != null) {
            return ResponseEntity.ok(commandeService.rechercherCommandesParClientEtStatut(clientId, statut));
        } else if (clientId != null) {
            return ResponseEntity.ok(commandeService.obtenirCommandesParClient(clientId));
        } else if (statut != null) {
            return ResponseEntity.ok(commandeService.rechercherCommandesParStatut(statut));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}