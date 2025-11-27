package com.smart.shop.controller;

import com.smart.shop.dto.Client.ClientCreateDto;
import com.smart.shop.dto.Client.ClientResponseDto;
import com.smart.shop.dto.Client.ClientUpdateDto;
import com.smart.shop.entity.Client;
import com.smart.shop.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody ClientCreateDto clientDto) {
        Client createdClient = clientService.createClient(clientDto);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable String id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(
            @PathVariable String id,
            @Valid @RequestBody ClientUpdateDto clientDto) {
        return ResponseEntity.ok(clientService.updateClient(id, clientDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable String id) {
        clientService.deleteClient(id);
    }

    // Endpoint supplémentaire pour les statistiques
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getClientStats(@PathVariable String id) {
        // Implémentez la logique des statistiques ici
        return ResponseEntity.ok().build();
    }
}