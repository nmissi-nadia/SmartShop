package com.smart.shop.repository;

import com.smart.shop.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    
    /**
     * Vérifie si un client existe avec l'email fourni
     * @param email L'email à vérifier
     * @return true si un client existe avec cet email, false sinon
     */
    boolean existsByEmail(String email);
    
    /**
     * Trouve un client par son email
     * @param email L'email du client à rechercher
     * @return Un Optional contenant le client s'il existe
     */
    Optional<Client> findByEmail(String email);
    
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
           "WHERE c.client.id = :clientId")
    Object[] getClientStats(@Param("clientId") String clientId);
    
    /**
     * Trouve les clients par niveau de fidélité
     * @param niveauFidelite Le niveau de fidélité
     * @return Liste des clients ayant ce niveau de fidélité
     */
    List<Client> findByNiveauFidelite(String niveauFidelite);
    
    /**
     * Vérifie si un client existe avec l'identifiant fourni
     * @param id L'identifiant du client
     * @return true si le client existe, false sinon
     */
    boolean existsById(String id);
    
    /**
     * Supprime un client par son identifiant
     * @param id L'identifiant du client à supprimer
     */
    void deleteById(String id);
    
    /**
     * Trouve un client par son identifiant
     * @param id L'identifiant du client
     * @return Un Optional contenant le client s'il existe
     */
    Optional<Client> findById(String id);
    
    /**
     * Récupère tous les clients
     * @return Liste de tous les clients
     */
    List<Client> findAll();
    
    /**
     * Compte le nombre total de clients
     * @return Le nombre de clients
     */
    long count();
}