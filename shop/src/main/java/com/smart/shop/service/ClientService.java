package com.smart.shop.service;

import com.smart.shop.dto.Client.ClientCreateDto;
import com.smart.shop.dto.Client.ClientResponseDto;
import com.smart.shop.dto.Client.ClientStatsDto;
import com.smart.shop.dto.Client.ClientUpdateDto;
import com.smart.shop.dto.Commande.CommandeResponseDto;
import com.smart.shop.entity.Client;
import com.smart.shop.entity.Commande;
import com.smart.shop.enums.CustomerTier;
import com.smart.shop.exception.ResourceAlreadyExistsException;
import com.smart.shop.exception.ResourceNotFoundException;
import com.smart.shop.mapper.ClientMapper;
import com.smart.shop.repository.ClientRepository;
import com.smart.shop.repository.CommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final CommandeRepository commandeRepository;
    private final ClientMapper clientMapper;

    @Transactional(readOnly = true)
    public ClientResponseDto obtenirClientParId(String id) {
        return clientRepository.findById(id)
                .map(clientMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID : " + id));
    }

    @Transactional(readOnly = true)
    public Client obtenirClientEntiteParId(String id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID : " + id));
    }

    @Transactional
    public ClientResponseDto creerClient(ClientCreateDto dto) {
        if (clientRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("Un client avec cet email existe déjà");
        }

        Client client = clientMapper.toEntity(dto);
        client.setNiveauFidelite(CustomerTier.BASIC); // Niveau par défaut
        return clientMapper.toDto(clientRepository.save(client));
    }

    @Transactional
    public ClientResponseDto mettreAJourClient(String id, ClientUpdateDto dto) {
        Client client = obtenirClientEntiteParId(id);
        clientMapper.updateFromDto(dto, client);
        return clientMapper.toDto(clientRepository.save(client));
    }

    @Transactional
    public void supprimerClient(String id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client non trouvé avec l'ID : " + id);
        }
        clientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ClientResponseDto> listerTousLesClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void mettreAJourStatistiquesClient(String clientId) {
        Client client = obtenirClientEntiteParId(clientId);
        
        // Calculer le nombre total de commandes
        long nombreCommandes = commandeRepository.countCommandesByClient_Id(clientId);
        
        // Calculer le montant total dépensé
        BigDecimal montantTotal = commandeRepository.findByClientId(clientId).stream()
                .map(Commande::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Mettre à jour le niveau de fidélité
        CustomerTier nouveauNiveau = calculerNiveauFidelite(nombreCommandes, montantTotal);
        
        client.setNombreCommandes(nombreCommandes);
        client.setMontantTotalDepense(montantTotal);
        client.setNiveauFidelite(nouveauNiveau);
        
        clientRepository.save(client);
    }

    private CustomerTier calculerNiveauFidelite(long nombreCommandes, BigDecimal montantTotal) {
        if (montantTotal.compareTo(new BigDecimal("5000")) >= 0 || nombreCommandes >= 20) {
            return CustomerTier.GOLD;
        } else if (montantTotal.compareTo(new BigDecimal("2000")) >= 0 || nombreCommandes >= 10) {
            return CustomerTier.SILVER;
        } else {
            return CustomerTier.BASIC;
        }
    }
}