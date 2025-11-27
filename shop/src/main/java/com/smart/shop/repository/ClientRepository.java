package com.smart.shop.repository;

import com.smart.shop.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Client> findByNiveauFidelite(String niveauFidelite);
    // méthodes exist by id
    boolean existsById(String id);
    //delete by id
    void deleteById(String id);
    //find by id
    Optional<Client> findById(String id);

}
