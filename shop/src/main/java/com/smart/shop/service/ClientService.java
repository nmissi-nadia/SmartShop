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
import com.smart.shop.mapper.CommandeMapper;
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
    private final CommandeMapper commandeMapper;

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

    @Transactional(readOnly = true)
    public List<CommandeResponseDto> consulterHistoriqueCommandes(String clientId) {
        // Valide que le client existe
        obtenirClientEntiteParId(clientId);

        List<Commande> commandes = commandeRepository.findByClientIdOrderByDateCommandeDesc(clientId);

        return commandes.stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void mettreAJourStatistiquesClient(String clientId) {
        Client client = obtenirClientEntiteParId(clientId);

        Object[] statsResult = commandeRepository.getClientStats(clientId);
        Object[] stats = (Object[]) statsResult[0];


        long nombreCommandes = (stats[0] instanceof Number) ? ((Number) stats[0]).longValue() : 0L;
        
        BigDecimal montantTotal = BigDecimal.ZERO;
        if (stats[1] instanceof Number) {
            montantTotal = new BigDecimal(((Number) stats[1]).toString());
        }

        LocalDateTime premiereCommande = (stats[2] instanceof LocalDateTime) ? (LocalDateTime) stats[2] : null;
        LocalDateTime derniereCommande = (stats[3] instanceof LocalDateTime) ? (LocalDateTime) stats[3] : null;


        // Mettre à jour le niveau de fidélité
        CustomerTier nouveauNiveau = calculerNiveauFidelite(nombreCommandes, montantTotal);
        
        client.setNombreCommandes(nombreCommandes);
        client.setMontantTotalDepense(montantTotal);
        client.setNiveauFidelite(nouveauNiveau);
        client.setDatePremiereCommande(premiereCommande);
        client.setDateDerniereCommande(derniereCommande);
        
        clientRepository.save(client);
    }

    private CustomerTier calculerNiveauFidelite(long nombreCommandes, BigDecimal montantTotal) {
        if (nombreCommandes >= 20 || (montantTotal != null && montantTotal.compareTo(new BigDecimal("15000")) >= 0)) {
            return CustomerTier.PLATINUM;
        } else if (nombreCommandes >= 10 || (montantTotal != null && montantTotal.compareTo(new BigDecimal("5000")) >= 0)) {
            return CustomerTier.GOLD;
        } else if (nombreCommandes >= 3 || (montantTotal != null && montantTotal.compareTo(new BigDecimal("1000")) >= 0)) {
            return CustomerTier.SILVER;
        } else {
            return CustomerTier.BASIC;
        }
    }
}