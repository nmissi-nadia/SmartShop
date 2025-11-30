package com.smart.shop.repository;

import com.smart.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsByNom(String nom);
    Optional<Product> findByNom(String nom);
}