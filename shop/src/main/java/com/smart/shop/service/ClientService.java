package com.smart.shop.service;

import com.smart.shop.dto.Client.*;
import com.smart.shop.entity.Client;
import com.smart.shop.entity.Commande;
import com.smart.shop.entity.User;
import com.smart.shop.enums.CustomerTier;
import com.smart.shop.enums.UserRole;
import com.smart.shop.exception.ResourceAlreadyExistsException;
import com.smart.shop.exception.ResourceNotFoundException;
import com.smart.shop.mapper.ClientMapper;
import com.smart.shop.mapper.CommandeMapper;
import com.smart.shop.repository.ClientRepository;
import com.smart.shop.repository.CommandeRepository;
import com.smart.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.smart.shop.config.PasswordConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.smart.shop.config.PasswordConfig.hashPassword;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final CommandeRepository commandeRepository;
    private final ClientMapper clientMapper;
    private final CommandeMapper commandeMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ClientResponseDto obtenirClientParId(String id) {
        return clientRepository.findById(id)
                .map(clientMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID : " + id));
    }

    @Transactional(readOnly = true)
    public Client obtenirClientEntiteParId(String id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID : " + id));
    }

    @Transactional
    public ClientResponseDto createClient(ClientCreateDto dto) {
        // Vérifier les contraintes d'unicité
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ResourceAlreadyExistsException("Un utilisateur avec ce nom d'utilisateur existe déjà");
        }
        if (clientRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("Un client avec cet email existe déjà");
        }

        // Mapper le DTO en entité Client. MapStruct gère les champs hérités de User.
        Client client = clientMapper.toEntity(dto);
        client.setPassword(hashPassword(dto.getPassword())); // Hasher le mot de passe
        client.setRole(UserRole.CLIENT); // Définir le rôle
        client.setNiveauFidelite(CustomerTier.BASIC); // Niveau par défaut
        
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponseDto(savedClient);
    }

    @Transactional
    public ClientResponseDto mettreAJourClient(String id, ClientUpdateDto dto) {
        Client client = obtenirClientEntiteParId(id);
        clientMapper.updateFromDto(dto, client);
        return clientMapper.toResponseDto(clientRepository.save(client));
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
                .map(clientMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClientOrderHistoryDto> consulterHistoriqueCommandes(String clientId) {
        // Valide que le client existe
        obtenirClientEntiteParId(clientId);

        List<Commande> commandes = commandeRepository.findByClientIdOrderByDateCommandeDesc(clientId);

        return commandes.stream()
                .map(clientMapper::commandeToClientOrderHistoryDto)
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
    @Transactional(readOnly = true)
    public ClientStatsDto obtenirStatsClient(String clientId) {
        Client client = obtenirClientEntiteParId(clientId);
        return new ClientStatsDto(
                client.getNombreCommandes(),
                client.getMontantTotalDepense(),
                client.getDatePremiereCommande(),
                client.getDateDerniereCommande(),
                client.getNiveauFidelite()
        );
    }

}