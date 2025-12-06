package com.smart.shop.service;

import com.smart.shop.dto.UserCreateDTO;
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

import static com.smart.shop.config.PasswordConfig.hashPassword;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional
    public User creerUser(UserCreateDTO dto) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Un utilisateur avec ce nom d'utilisateur existe déjà");
        }

        // Créer et sauvegarder l'utilisateur
        User user = User.builder()
                .username(dto.getUsername())
                .password(hashPassword(dto.getPassword()))
                .role(dto.getRole() != null ? dto.getRole() : UserRole.CLIENT) // Valeur par défaut
                .build();

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
    public User updateUser(String id, UserCreateDTO dto) {
        // Vérifier si l'utilisateur existe
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        // Vérifier si le nouveau nom d'utilisateur est déjà utilisé par un autre utilisateur
        if (!existingUser.getUsername().equals(dto.getUsername()) &&
                userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà utilisé");
        }

        // Mettre à jour les champs
        existingUser.setUsername(dto.getUsername());

        // Mettre à jour le mot de passe uniquement s'il est fourni
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existingUser.setPassword(hashPassword(dto.getPassword()));
        }

        // Mettre à jour le rôle s'il est fourni, sinon conserver l'ancien
        if (dto.getRole() != null) {
            existingUser.setRole(dto.getRole());
        }

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