package com.smart.shop.controller;

import com.smart.shop.dto.Paiement.PaiementCreateDto;
import com.smart.shop.dto.Paiement.PaiementResponseDto;
import com.smart.shop.dto.Paiement.PaiementStatutUpdateDto;
import com.smart.shop.service.PaiementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    public ResponseEntity<PaiementResponseDto> creerPaiement(@Valid @RequestBody PaiementCreateDto dto) {
        return new ResponseEntity<>(paiementService.creerPaiement(dto), HttpStatus.CREATED);
    }
    @PutMapping("/{id}/statut")
    public ResponseEntity<PaiementResponseDto> mettreAJourStatutPaiement(
            @PathVariable String id,
            @Valid @RequestBody PaiementStatutUpdateDto dto) {
        return ResponseEntity.ok(paiementService.mettreAJourStatutPaiement(id, dto));
    }
}
