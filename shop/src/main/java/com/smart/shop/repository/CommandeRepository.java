package com.smart.shop.repository;

import com.smart.shop.entity.Commande;
import com.smart.shop.enums.StatutCommande;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, String> {
    
    /**
     * Trouve toutes les commandes d'un client triées par date de commande décroissante
     * @param clientId L'identifiant du client
     * @return Liste des commandes du client
     */
    List<Commande> findByClientIdOrderByDateCommandeDesc(String clientId);
    
    /**
     * Trouve les commandes d'un client par statut
     * @param clientId L'identifiant du client
     * @param statut Le statut des commandes à rechercher
     * @return Liste des commandes correspondantes
     */
    List<Commande> findByClientIdAndStatut(String clientId, String statut);
    
    /**
     * Vérifie si un client a des commandes
     * @param clientId L'identifiant du client
     * @return true si le client a des commandes, false sinon
     */
    boolean existsByClientId(String clientId);
    
    /**
     * Récupère les statistiques d'un client
     * @param clientId L'identifiant du client
     * @return Un tableau d'objets contenant les statistiques
     */
    @Query("SELECT " +
           "COUNT(c) as nombreCommandes, " +
           "COALESCE(SUM(c.total), 0) as montantTotal, " +
           "MIN(c.dateCommande) as premiereCommande, " +
           "MAX(c.dateCommande) as derniereCommande " +
           "FROM Commande c " +
           "WHERE c.client.id = :clientId AND c.statut = com.smart.shop.enums.StatutCommande.CONFIRMED")
    Object[] getClientStats(@Param("clientId") String clientId);
    
    /**
     * Trouve les commandes entre deux dates
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Liste des commandes dans l'intervalle
     */
    List<Commande> findByDateCommandeBetween(LocalDateTime dateDebut, LocalDateTime dateFin);
    List<Commande> findByStatut(StatutCommande statut);

    List<Commande> findByClientIdAndStatut(String clientId, StatutCommande statut);
}