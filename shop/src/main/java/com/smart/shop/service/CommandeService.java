package com.smart.shop.service;

import com.smart.shop.dto.Client.ClientResponseDto;
import com.smart.shop.dto.Commande.CommandeCreateDto;
import com.smart.shop.dto.Commande.CommandeResponseDto;
import com.smart.shop.entity.*;
import com.smart.shop.enums.PaymentStatus;
import com.smart.shop.enums.StatutCommande;
import com.smart.shop.exception.CommandeException.StockInsuffisantException;
import com.smart.shop.exception.ResourceNotFoundException;
import com.smart.shop.mapper.CommandeMapper;
import com.smart.shop.repository.CommandeRepository;
import com.smart.shop.repository.OrderItemRepository;
import com.smart.shop.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ClientService clientService;
    private final ProductService productService;
    private final CommandeMapper commandeMapper;
    private final OrderItemRepository orderItemRepository;
    private final PaiementRepository paiementRepository;

    // -----------------------------------------------------------------
    //                           CRÉATION
    // -----------------------------------------------------------------
    @Transactional
    public CommandeResponseDto creerCommande(CommandeCreateDto dto) {

        Client client = clientService.obtenirClientEntiteParId(dto.getClientId());

        Commande commande = new Commande(client);
        commande.setCodePromo(dto.getCodePromo());
        commande.setDateCommande(LocalDateTime.now());
        commande.setStatut(StatutCommande.PENDING);

        // 1. Ajout des items
        dto.getItems().forEach(ligne ->
                ajouterLigneCommande(commande, ligne)
        );

        // 2. Remises avant calcul total
        appliquerRemises(commande, client);

        // 3. Calcul des totaux finaux
        commande.calculerTotaux();

        // 4. Sauvegarde (cascade ALL gère les OrderItems)
        Commande saved = commandeRepository.save(commande);

        return commandeMapper.toDto(saved);
    }

    private void ajouterLigneCommande(Commande commande, CommandeCreateDto.LigneCommandeDto ligneDto) {

        Product produit = productService.obtenirProduitEntiteParId(ligneDto.getProduitId());

        if (!productService.verifierStockDisponible(produit.getId(), ligneDto.getQuantite())) {
            throw new StockInsuffisantException(
                    "Stock insuffisant pour " + produit.getNom() +
                            " | Disponible: " + produit.getStockDisponible() +
                            " | Demandé: " + ligneDto.getQuantite()
            );
        }

        if (produit.getPrixUnitaire() == null) {
            throw new IllegalStateException("Le produit " + produit.getNom() + " n'a pas de prix défini");
        }

        OrderItem item = new OrderItem();
        item.setProduit(produit);
        item.setQuantite(ligneDto.getQuantite());
        item.setPrixUnitaire(produit.getPrixUnitaire());
        item.calculerTotalLigne();

        // LA RELATION CRITIQUE ⬇
        commande.ajouterItem(item);
    }

    private void appliquerRemises(Commande commande, Client client) {

        BigDecimal montantRemise = BigDecimal.ZERO;

        BigDecimal sousTotal = commande.getItems().stream()
                .map(OrderItem::getTotalLigne)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sousTotal == null)
            sousTotal = BigDecimal.ZERO;

        // 1. Remise fidélité
        if (client.getNiveauFidelite() != null && sousTotal.compareTo(BigDecimal.ZERO) > 0) {
            switch (client.getNiveauFidelite()) {
                case SILVER:
                    if (sousTotal.compareTo(new BigDecimal("500")) >= 0)
                        montantRemise = sousTotal.multiply(new BigDecimal("0.05"));
                    break;

                case GOLD:
                    if (sousTotal.compareTo(new BigDecimal("800")) >= 0)
                        montantRemise = sousTotal.multiply(new BigDecimal("0.10"));
                    break;

                case PLATINUM:
                    if (sousTotal.compareTo(new BigDecimal("1200")) >= 0)
                        montantRemise = sousTotal.multiply(new BigDecimal("0.15"));
                    break;

                default:
                    break;
            }
        }

        // 2. Remise promo (si aucune remise fidélité appliquée)
        if (montantRemise.compareTo(BigDecimal.ZERO) == 0 && commande.getCodePromo() != null) {
            if (commande.getCodePromo().matches("PROMO-[A-Z0-9]{4}")) {
                montantRemise = sousTotal.multiply(new BigDecimal("0.05"));
            }
        }

        commande.setRemise(
                montantRemise.setScale(2, RoundingMode.HALF_UP)
        );
    }

    // -----------------------------------------------------------------
    //                           RECHERCHES
    // -----------------------------------------------------------------
    @Transactional(readOnly = true)
    public CommandeResponseDto obtenirCommandeParId(String id) {
        return commandeRepository.findById(id)
                .map(commandeMapper::toDto)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Commande non trouvée avec l'ID : " + id)
                );
    }

    @Transactional(readOnly = true)
    public List<CommandeResponseDto> obtenirCommandesParClient(String clientId) {
        return commandeRepository.findByClientIdOrderByDateCommandeDesc(clientId).stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommandeResponseDto> rechercherCommandesParStatut(StatutCommande statut) {
        return commandeRepository.findByStatut(statut).stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommandeResponseDto> rechercherCommandesParClientEtStatut(String clientId, StatutCommande statut) {
        return commandeRepository.findByClientIdAndStatut(clientId, statut).stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------------
    //                           MISE À JOUR STATUT
    // -----------------------------------------------------------------
    @Transactional
    public CommandeResponseDto mettreAJourStatut(String id, StatutCommande nouveauStatut) {

        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Commande non trouvée avec l'ID : " + id)
                );

        switch (nouveauStatut) {

            case CONFIRMED:
                if (commande.getStatut() != StatutCommande.PENDING)
                    throw new IllegalStateException("Seule une commande PENDING peut être confirmée.");

                // Vérification 1 : Le montant restant doit être à zéro.
                if (commande.getMontantRestant().compareTo(BigDecimal.ZERO) > 0) {
                    throw new IllegalStateException("Commande non entièrement payée.");
                }

                // Vérification 2 : Tous les paiements doivent être encaissés.
                List<Paiement> paiements = paiementRepository.findByCommandeId(id);
                boolean tousPaiementsEncaisse = paiements.stream().allMatch(p -> p.getStatut() == PaymentStatus.ENCAISSÉ);
                if (!tousPaiementsEncaisse) {
                    throw new IllegalStateException("Impossible de confirmer : tous les paiements ne sont pas encore encaissés.");
                }

                // Décrémenter stock
                commande.getItems().forEach(item ->
                        productService.mettreAJourStock(item.getProduit().getId(), -item.getQuantite())
                );

                // Mettre à jour stats client
                clientService.mettreAJourStatistiquesClient(commande.getClient().getId());
                break;

            case CANCELED:
                // On peut annuler une commande en attente ou déjà confirmée.
                if (commande.getStatut() != StatutCommande.PENDING && commande.getStatut() != StatutCommande.CONFIRMED) {
                    throw new IllegalStateException("Seule une commande PENDING ou CONFIRMED peut être annulée.");
                }

                // Si la commande était confirmée, on doit réapprovisionner le stock.
                if (commande.getStatut() == StatutCommande.CONFIRMED) {
                    commande.getItems().forEach(item ->
                            productService.mettreAJourStock(item.getProduit().getId(), item.getQuantite()) // Note: quantité positive
                    );
                    // Note : Ici, il faudrait aussi gérer la logique de remboursement des paiements.
                    // Pour l'instant, nous nous concentrons sur le stock.
                }

                break;

            default:
                throw new IllegalArgumentException("Transition non supportée : " + nouveauStatut);
        }

        commande.setStatut(nouveauStatut);

        return commandeMapper.toDto(commandeRepository.save(commande));
    }
}
