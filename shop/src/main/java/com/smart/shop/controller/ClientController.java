package com.smart.shop.controller;

import com.smart.shop.dto.Client.*;
import com.smart.shop.dto.Commande.CommandeResponseDto;
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
    public ResponseEntity<ClientResponseDto> creerClient(@Valid @RequestBody ClientCreateDto dto) {
        return new ResponseEntity<>(clientService.creerClient(dto), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> obtenirClient(@PathVariable String id) {
        return ResponseEntity.ok(clientService.obtenirClientParId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> obtenirTousLesClients() {
        return ResponseEntity.ok(clientService.listerTousLesClients());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> mettreAJourClient(
            @PathVariable String id,
            @Valid @RequestBody ClientUpdateDto dto) {
        return ResponseEntity.ok(clientService.mettreAJourClient(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerClient(@PathVariable String id) {
        clientService.supprimerClient(id);
    }
    

}