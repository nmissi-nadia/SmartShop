package com.smart.shop.service;

import com.smart.shop.dto.Client.ClientCreateDto;
import com.smart.shop.dto.Client.ClientUpdateDto;
import com.smart.shop.entity.Client;
import com.smart.shop.exception.ResourceNotFoundException;
import com.smart.shop.mapper.ClientMapper;
import com.smart.shop.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {
    
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UserService userService;

    @Transactional
    public Client createClient(ClientCreateDto clientDto) {
        Client client = clientMapper.toEntity(clientDto);
        // Ici, vous pouvez ajouter une logique métier spécifique
        return clientRepository.save(client);
    }

    @Transactional(readOnly = true)
    public Client getClientById(String id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Transactional
    public Client updateClient(String id, ClientUpdateDto clientDto) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        
        // Mise à jour des champs non nuls
        clientMapper.updateFromDto(clientDto, existingClient);
        
        return clientRepository.save(existingClient);
    }

    @Transactional
    public void deleteClient(String id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }
} 
