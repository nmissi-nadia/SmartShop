package com.smart.shop.controller;

import com.smart.shop.dto.Product.*;
import com.smart.shop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    public ResponseEntity<ProductResponseDto> creerProduit(@Valid @RequestBody ProductCreateDto dto) {
        return new ResponseEntity<>(productService.creerProduit(dto), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> obtenirProduit(@PathVariable String id) {
        return ResponseEntity.ok(productService.obtenirProduitParId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> obtenirTousLesProduits() {
        return ResponseEntity.ok(productService.obtenirTousLesProduits());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> mettreAJourProduit(
            @PathVariable String id,
            @Valid @RequestBody ProductUpdateDto dto) {
        return ResponseEntity.ok(productService.mettreAJourProduit(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerProduit(@PathVariable String id) {
        productService.supprimerProduit(id);
    }
}