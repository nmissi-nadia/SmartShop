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
import java.util.Arrays;
import java.util.List;

@Component
@Order(1)
public class AuthFilter extends HttpFilter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register",
        "/error",
        "/swagger-ui.html",
        "/swagger-ui/"
    );

    @Override
    protected void doFilter(HttpServletRequest request, 
                          HttpServletResponse response, 
                          FilterChain chain) throws IOException, ServletException {
        
        String path = request.getRequestURI();
        
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        
        if (isPublicPath) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Non authentifié\"}");
            return;
        }

        // Vérification des rôles si nécessaire
        String requiredRole = getRequiredRole(path);
        if (requiredRole != null) {
            String userRole = (String) session.getAttribute("USER_ROLE");
            if (!requiredRole.equals(userRole)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Accès refusé\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String getRequiredRole(String path) {
        if (path.startsWith("/api/admin/")) {
            return "ADMIN";
        }
        return null;
    }
}