package com.smart.shop.controller;

import com.smart.shop.config.RoleRequired;
import com.smart.shop.dto.Product.*;
import com.smart.shop.enums.UserRole;
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
    @RoleRequired({UserRole.ADMIN, UserRole.CLIENT})
    public ResponseEntity<ProductResponseDto> creerProduit(@Valid @RequestBody ProductCreateDto dto) {
        return new ResponseEntity<>(productService.creerProduit(dto), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @RoleRequired(UserRole.ADMIN)
    public ResponseEntity<ProductResponseDto> obtenirProduit(@PathVariable String id) {
        return ResponseEntity.ok(productService.obtenirProduitParId(id));
    }
    
    @GetMapping
    @RoleRequired({UserRole.ADMIN, UserRole.CLIENT})
    public ResponseEntity<List<ProductResponseDto>> obtenirTousLesProduits() {
        return ResponseEntity.ok(productService.obtenirTousLesProduits());
    }
    
    @PutMapping("/{id}")
    @RoleRequired({UserRole.ADMIN, UserRole.CLIENT})
    public ResponseEntity<ProductResponseDto> mettreAJourProduit(
            @PathVariable String id,
            @Valid @RequestBody ProductUpdateDto dto) {
        return ResponseEntity.ok(productService.mettreAJourProduit(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @RoleRequired(UserRole.ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerProduit(@PathVariable String id) {
        productService.supprimerProduit(id);
    }
}