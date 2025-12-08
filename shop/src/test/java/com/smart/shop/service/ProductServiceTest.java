package com.smart.shop.service;

import com.smart.shop.dto.Product.*;
import com.smart.shop.entity.Product;
import com.smart.shop.exception.CommandeException;
import com.smart.shop.exception.ResourceAlreadyExistsException;
import com.smart.shop.exception.ResourceNotFoundException;
import com.smart.shop.mapper.ProductMapper;
import com.smart.shop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductMapper productMapper;
    private ProductService productService;

    @BeforeEach
    void setup() {
        productRepository = mock(ProductRepository.class);
        productMapper = mock(ProductMapper.class);
        productService = new ProductService(productRepository, productMapper);
    }

    //          OBTENIR PRODUIT PAR ID
    @Test
    void obtenirProduitParId_retourneProduit() {
        Product p = new Product();
        p.setId("1");

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId("1");

        when(productRepository.findById("1")).thenReturn(Optional.of(p));
        when(productMapper.toDto(p)).thenReturn(dto);

        ProductResponseDto result = productService.obtenirProduitParId("1");

        assertEquals("1", result.getId());
    }

    @Test
    void obtenirProduitParId_exceptionSiNonTrouve() {
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                productService.obtenirProduitParId("999"));
    }


    //          CREATION PRODUIT

    @Test
    void creerProduit_ok() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setNom("Test Produit");

        Product p = new Product();
        p.setNom("Test Produit");

        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setNom("Test Produit");

        when(productRepository.existsByNom("Test Produit")).thenReturn(false);
        when(productMapper.toEntity(dto)).thenReturn(p);
        when(productRepository.save(p)).thenReturn(p);
        when(productMapper.toDto(p)).thenReturn(responseDto);

        ProductResponseDto result = productService.creerProduit(dto);

        assertEquals("Test Produit", result.getNom());
    }

    @Test
    void creerProduit_exceptionSiNomExiste() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setNom("Existe");

        when(productRepository.existsByNom("Existe")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () ->
                productService.creerProduit(dto));
    }

    //          MISE À JOUR PRODUIT

    @Test
    void mettreAJourProduit_ok() {
        Product p = new Product();
        p.setId("1");

        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setNom("Updated");

        ProductResponseDto resp = new ProductResponseDto();
        resp.setNom("Updated");

        when(productRepository.findById("1")).thenReturn(Optional.of(p));
        doNothing().when(productMapper).updateFromDto(dto, p);
        when(productRepository.save(p)).thenReturn(p);
        when(productMapper.toDto(p)).thenReturn(resp);

        ProductResponseDto result = productService.mettreAJourProduit("1", dto);

        assertEquals("Updated", result.getNom());
    }

    @Test
    void mettreAJourProduit_exceptionSiNonTrouve() {
        ProductUpdateDto dto = new ProductUpdateDto();

        when(productRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                productService.mettreAJourProduit("999", dto));
    }


    //          SUPPRESSION

    @Test
    void supprimerProduit_ok() {
        when(productRepository.existsById("1")).thenReturn(true);

        productService.supprimerProduit("1");

        verify(productRepository, times(1)).deleteById("1");
    }

    @Test
    void supprimerProduit_exceptionSiNonTrouve() {
        when(productRepository.existsById("999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                productService.supprimerProduit("999"));
    }

    //          STOCK

    @Test
    void mettreAJourStock_ok() {
        Product p = new Product();
        p.setId("1");
        p.setStockDisponible(10);

        when(productRepository.findById("1")).thenReturn(Optional.of(p));
        when(productRepository.save(p)).thenReturn(p);

        productService.mettreAJourStock("1", -3); // produit → stock = 7

        assertEquals(7, p.getStockDisponible());
    }

    @Test
    void mettreAJourStock_exceptionStockInsuffisant() {
        Product p = new Product();
        p.setId("1");
        p.setStockDisponible(2);

        when(productRepository.findById("1")).thenReturn(Optional.of(p));

        assertThrows(CommandeException.StockInsuffisantException.class, () ->
                productService.mettreAJourStock("1", -5));
    }

    @Test
    void verifierStockDisponible_retourneVraiOuFaux() {
        Product p = new Product();
        p.setId("1");
        p.setStockDisponible(10);

        when(productRepository.findById("1")).thenReturn(Optional.of(p));

        assertTrue(productService.verifierStockDisponible("1", 5));
        assertFalse(productService.verifierStockDisponible("1", 20));
    }
}
