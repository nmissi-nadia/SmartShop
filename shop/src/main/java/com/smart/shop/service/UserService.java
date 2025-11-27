package com.smart.shop.service;

import com.smart.shop.dto.UserCreateDto;
import com.smart.shop.dto.Client.ClientCreateDto;
import com.smart.shop.entity.*;
import com.smart.shop.enums.*;
import com.smart.shop.exception.ResourceAlreadyExistsException;
import com.smart.shop.exception.ResourceNotFoundException;
import com.smart.shop.mapper.ClientMapper;
import com.smart.shop.mapper.UserMapper;
import com.smart.shop.repository.ClientRepository;
import com.smart.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional
    public User createUser(UserCreateDto userDto) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new ResourceAlreadyExistsException("Un utilisateur avec ce nom d'utilisateur existe déjà");
        }
        // Créer l'utilisateur de base
        User user = userMapper.toEntity(userDto);
        // Définir le rôle par défaut si non spécifié
        if (user.getRole() == null) {
            user.setRole(UserRole.CLIENT);
        }

        // Si c'est un client, créer également l'entité Client
        if (user.getRole() == UserRole.CLIENT && userDto instanceof ClientCreateDto) {
            ClientCreateDto clientDto = (ClientCreateDto) userDto;
            
            // Vérifier si l'email est déjà utilisé
            if (clientRepository.existsByEmail(clientDto.getEmail())) {
                throw new ResourceAlreadyExistsException("Un client avec cet email existe déjà");
            }
            // Créer le client
            Client client = clientMapper.toEntity(clientDto);
            client.setId(user.getId()); // Utiliser le même ID que l'utilisateur
            clientRepository.save(client);
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(String id, UserCreateDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Mise à jour des champs
        existingUser.setUsername(userDto.getUsername());
        existingUser.setRole(userDto.getRole());

        
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}