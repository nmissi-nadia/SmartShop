package com.smart.shop.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Order(1)
public class AuthFilter extends HttpFilter {

    // IMPORTANT :
    // startsWith() ne comprend pas les wildcards "**"
    // donc on liste seulement les préfixes réels.
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth",       // login, register
            "/swagger-ui",     // UI Swagger
            "/v3/api-docs",    // JSON Swagger
            "/swagger-resources",
            "/webjars",
            "/error"
    );

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain)
            throws IOException, ServletException {

        String path = request.getRequestURI();

        // Laisse passer toutes les requêtes OPTIONS (CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Vérification si l’URL est publique
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        if (isPublic) {
            chain.doFilter(request, response);
            return;
        }

        // Vérification session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Non authentifié\"}");
            return;
        }

        // Vérification rôle si nécessaire
        String requiredRole = getRequiredRole(path);
        if (requiredRole != null) {
            String userRole = (String) session.getAttribute("USER_ROLE");

            if (userRole == null || !userRole.equals(requiredRole)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Accès refusé\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    // Gestion simple des permissions
    private String getRequiredRole(String path) {

        // Les routes admin nécessitent rôle ADMIN
        if (path.startsWith("/api/admin/")) {
            return "ADMIN";
        }

        return null;
    }
}
