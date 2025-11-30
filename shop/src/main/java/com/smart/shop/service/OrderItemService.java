package com.smart.shop.service;

import com.smart.shop.dto.OrderItem.OrderItemResponseDto;
import com.smart.shop.entity.OrderItem;
import com.smart.shop.exception.CommandeException;
import com.smart.shop.exception.ResourceNotFoundException;
import com.smart.shop.mapper.OrderItemMapper;
import com.smart.shop.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final OrderItemMapper orderItemMapper;

    @Transactional(readOnly = true)
    public OrderItemResponseDto obtenirOrderItemParId(String id) {
        return orderItemRepository.findById(id)
                .map(orderItemMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Ligne de commande non trouvée avec l'ID : " + id));
    }

    @Transactional
    public void supprimerOrderItem(String id) {
        if (!orderItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ligne de commande non trouvée avec l'ID : " + id);
        }
        orderItemRepository.deleteById(id);
    }

    @Transactional
    public void mettreAJourQuantite(String orderItemId, int nouvelleQuantite) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Ligne de commande non trouvée avec l'ID : " + orderItemId));
        
        // Calculer la différence de quantité
        int difference = nouvelleQuantite - item.getQuantite();
        
        // Vérifier le stock
        if (!productService.verifierStockDisponible(item.getProduit().getId(), difference)) {
            throw new CommandeException.StockInsuffisantException("Stock insuffisant pour cette quantité");
        }
        
        // Mettre à jour la quantité
        item.setQuantite(nouvelleQuantite);
        item.calculerTotalLigne();
        
        // Mettre à jour le stock
        productService.mettreAJourStock(item.getProduit().getId(), -difference);
        
        orderItemRepository.save(item);
        
        // Mettre à jour la commande parente
        if (item.getCommande() != null) {
            item.getCommande().calculerTotaux();
            // La commande sera sauvegardée automatiquement grâce à CascadeType.ALL
        }
    }
}
