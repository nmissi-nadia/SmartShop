package com.smart.shop.repository;

import com.smart.shop.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, String> {
    List<Paiement> findByCommandeId(String commandeId);
    @Query("SELECT COALESCE(MAX(p.numeroPaiement), 0) FROM Paiement p WHERE p.commande.id = :commandeId")
    int findLastPaymentNumber(String commandeId);

}
