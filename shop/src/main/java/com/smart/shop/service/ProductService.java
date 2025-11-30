package com.smart.shop.service;

import com.smart.shop.dto.Product.*;
import com.smart.shop.entity.Product;
import com.smart.shop.exception.ResourceAlreadyExistsException;
import com.smart.shop.exception.ResourceNotFoundException;
import com.smart.shop.mapper.ProductMapper;
import com.smart.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public ProductResponseDto obtenirProduitParId(String id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));
    }

    @Transactional(readOnly = true)
    public Product obtenirProduitEntiteParId(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));
    }

    @Transactional
    public ProductResponseDto creerProduit(ProductCreateDto dto) {
        if (productRepository.existsByNom(dto.getNom())) {
            throw new ResourceAlreadyExistsException("Un produit avec ce nom existe déjà");
        }
        Product produit = productMapper.toEntity(dto);
        return productMapper.toDto(productRepository.save(produit));
    }

    @Transactional
    public ProductResponseDto mettreAJourProduit(String id, ProductUpdateDto dto) {
        Product produit = obtenirProduitEntiteParId(id);
        productMapper.updateFromDto(dto, produit);
        return productMapper.toDto(productRepository.save(produit));
    }

    @Transactional
    public void supprimerProduit(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> listerTousLesProduits() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void mettreAJourStock(String produitId, int quantite) {
        Product produit = obtenirProduitEntiteParId(produitId);
        int nouveauStock = produit.getStockDisponible() + quantite;
        if (nouveauStock < 0) {
            throw new StockInsuffisantException("Stock insuffisant pour le produit : " + produit.getNom());
        }
        produit.setStockDisponible(nouveauStock);
        productRepository.save(produit);
    }

    @Transactional(readOnly = true)
    public boolean verifierStockDisponible(String produitId, int quantiteDemandee) {
        return productRepository.findById(produitId)
                .map(produit -> produit.getStockDisponible() >= quantiteDemandee)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + produitId));
    }
}