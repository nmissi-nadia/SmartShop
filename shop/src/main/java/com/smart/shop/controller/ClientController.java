package com.smart.shop.controller;

import com.smart.shop.config.RoleRequired;
import com.smart.shop.dto.Client.*;
import com.smart.shop.enums.UserRole;
import com.smart.shop.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    
    private final ClientService clientService;

    @PostMapping
    @RoleRequired(UserRole.ADMIN)
    public ResponseEntity<ClientResponseDto> creerClient(@Valid @RequestBody ClientCreateDto dto) {
        ClientResponseDto nouveauClient = clientService.createClient(dto);
        return new ResponseEntity<>(nouveauClient, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    @RoleRequired({UserRole.ADMIN, UserRole.CLIENT})
    public ResponseEntity<ClientResponseDto> obtenirClient(@PathVariable String id) {
        return ResponseEntity.ok(clientService.obtenirClientParId(id));
    }
    @GetMapping
    @RoleRequired(UserRole.ADMIN)
    public ResponseEntity<List<ClientResponseDto>> obtenirTousLesClients() {
        return ResponseEntity.ok(clientService.listerTousLesClients());
    }
    @PutMapping("/{id}")
    @RoleRequired({UserRole.ADMIN, UserRole.CLIENT})
    public ResponseEntity<ClientResponseDto> mettreAJourClient(
            @PathVariable String id,
            @Valid @RequestBody ClientUpdateDto dto) {
        return ResponseEntity.ok(clientService.mettreAJourClient(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RoleRequired(UserRole.ADMIN)
    public void supprimerClient(@PathVariable String id) {
        clientService.supprimerClient(id);
    }
    @GetMapping("/{id}/stats")
    @RoleRequired({UserRole.ADMIN, UserRole.CLIENT})
    public ResponseEntity<ClientStatsDto> obtenirStatsClient(@PathVariable String id) {
        // Mettre à jour les stats avant la récupération
        clientService.mettreAJourStatistiquesClient(id);
        ClientStatsDto stats = clientService.obtenirStatsClient(id);
        return ResponseEntity.ok(stats);
    }


}