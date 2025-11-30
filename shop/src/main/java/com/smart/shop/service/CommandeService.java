package com.smart.shop.service;

import com.smart.shop.dto.Client.ClientResponseDto;
import com.smart.shop.dto.Commande.CommandeCreateDto;
import com.smart.shop.dto.Commande.CommandeResponseDto;
import com.smart.shop.entity.*;
import com.smart.shop.enums.StatutCommande;
import com.smart.shop.exception.*;
import com.smart.shop.exception.CommandeException.StockInsuffisantException;
import com.smart.shop.mapper.CommandeMapper;
import com.smart.shop.repository.CommandeRepository;
import com.smart.shop.repository.OrderItemRepository;
import com.smart.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private final ClientService clientService;
    private final ProductService productService;
    private final CommandeMapper commandeMapper;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public CommandeResponseDto creerCommande(CommandeCreateDto dto) {
        // 1. Vérifier et récupérer le client
        Client client = clientService.obtenirClientEntiteParId(dto.getClientId());
        
        // 2. Créer la commande
        Commande commande = commandeMapper.toEntity(dto);
        commande.setClient(client);
        commande.setDateCommande(LocalDateTime.now());
        commande.setStatut(StatutCommande.PENDING);
        
        // 3. Ajouter les articles et gérer les stocks
        for (CommandeCreateDto.LigneCommandeDto ligneDto : dto.getLignesCommande()) {
            ajouterLigneCommande(commande, ligneDto);
        }
        
        // 4. Calculer les totaux
        commande.calculerTotaux();
        
        // 5. Appliquer les remises
        appliquerRemises(commande, client);
        
        // 6. Sauvegarder la commande
        Commande commandeSauvegardee = commandeRepository.save(commande);
        
        // 7. Mettre à jour les statistiques du client
        clientService.mettreAJourStatistiquesClient(client.getId());
        
        return commandeMapper.toDto(commandeSauvegardee);
    }

    private void ajouterLigneCommande(Commande commande, CommandeCreateDto.LigneCommandeDto ligneDto) {
        Product produit = productService.obtenirProduitEntiteParId(ligneDto.getProduitId());
        
        // Vérifier le stock
        if (!productService.verifierStockDisponible(produit.getId(), ligneDto.getQuantite())) {
            throw new StockInsuffisantException(
                String.format("Stock insuffisant pour %s. Disponible: %d, Demandé: %d",
                    produit.getNom(), 
                    produit.getStockDisponible(), 
                    ligneDto.getQuantite())
            );
        }
        
        // Créer la ligne de commande
        OrderItem item = new OrderItem();
        item.setProduit(produit);
        item.setQuantite(ligneDto.getQuantite());
        item.setPrixUnitaire(produit.getPrixUnitaire());
        item.calculerTotalLigne();
        
        commande.ajouterItem(item);
        
        // Mettre à jour le stock
        productService.mettreAJourStock(produit.getId(), -ligneDto.getQuantite());
    }

    private void appliquerRemises(Commande commande, Client client) {
        BigDecimal remiseTotale = BigDecimal.ZERO;
        
        // 1. Remise fidélité
        if (client.getNiveauFidelite() != null) {
            switch (client.getNiveauFidelite()) {
                case BASIC -> remiseTotale = remiseTotale.add(new BigDecimal("5.00")); // 5%
                case SILVER -> remiseTotale = remiseTotale.add(new BigDecimal("10.00")); // 10%
                case GOLD -> remiseTotale = remiseTotale.add(new BigDecimal("15.00")); // 15%
            }
        }
        
        // 2. Code promo
        if (commande.getCodePromo() != null && commande.getCodePromo().equals("PROMO-5")) {
            remiseTotale = remiseTotale.add(new BigDecimal("5.00")); // +5%
        }
        
        // 3. Appliquer la remise totale
        if (remiseTotale.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal montantRemise = commande.getSousTotal()
                .multiply(remiseTotale)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            commande.setRemise(montantRemise);
        }
    }

    @Transactional(readOnly = true)
    public CommandeResponseDto obtenirCommandeParId(String id) {
        return commandeRepository.findById(id)
                .map(commandeMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID : " + id));
    }

    @Transactional(readOnly = true)
    public List<CommandeResponseDto> obtenirCommandesParClient(String clientId) {
        return commandeRepository.findByClientIdOrderByDateCommandeDesc(clientId).stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void mettreAJourStatut(String id, StatutCommande nouveauStatut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID : " + id));
        
        // Vérifier les transitions de statut valides
        if (commande.getStatut() == StatutCommande.CANCELED ||
            commande.getStatut() == StatutCommande.CONFIRMED) {
            throw new IllegalStateException("Impossible de modifier une commande " + commande.getStatut());
        }
        
        commande.setStatut(nouveauStatut);
        commandeRepository.save(commande);
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
}