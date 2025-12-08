package com.smart.shop.controller;

import com.smart.shop.config.RoleRequired;
import com.smart.shop.dto.auth.LoginRequest;
import com.smart.shop.entity.User;
import com.smart.shop.enums.UserRole;
import com.smart.shop.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // --- Login ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
                                   HttpServletRequest request) {
        try {
            // Authentification via le service
            User user = authService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            // Création de la session
            HttpSession session = request.getSession(true);
            session.setAttribute("USER_ID", user.getId());
            session.setAttribute("USER_ROLE", user.getRole());
            session.setAttribute("USER_OBJECT", user); // stocke l'objet complet

            return ResponseEntity.ok(Map.of(
                    "message", "Connexion réussie",
                    "user", user
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Identifiants invalides"));
        }
    }

    // --- Logout ---
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }

    // --- Endpoint pour récupérer l'utilisateur courant ---
    @GetMapping("/me")
    @RoleRequired({UserRole.ADMIN, UserRole.CLIENT}) // accessible aux deux rôles
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Non authentifié"));
        }

        User user = (User) session.getAttribute("USER_OBJECT");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Non authentifié"));
        }

        return ResponseEntity.ok(user);
    }
}
