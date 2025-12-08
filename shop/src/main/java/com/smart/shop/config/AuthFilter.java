package com.smart.shop.config;

import com.smart.shop.enums.UserRole;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.lang.reflect.Method;

@Component
@WebFilter("/*")
public class AuthFilter implements Filter {

    private final RequestMappingHandlerMapping handlerMapping;

    public AuthFilter(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("USER_ROLE") == null) {
            sendError(httpResponse, 401, "Session expirée ou utilisateur non authentifié");
            return;
        }

        UserRole userRole = (UserRole) session.getAttribute("USER_ROLE");

        // 3️⃣ Vérification des rôles via annotation
        if (isAccessDenied(httpRequest, userRole)) {
            sendError(httpResponse, 403, "Accès refusé : droits insuffisants");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/") ||
               path.equals("/api/health") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs");
    }

    private boolean isAccessDenied(HttpServletRequest request, UserRole userRole) {
        // ADMIN a toujours accès à tout
        if (userRole == UserRole.ADMIN) return false;

        try {
            Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
            if (handler instanceof HandlerMethod hm) {
                // Vérifie annotation sur la méthode
                RoleRequired roleMethod = hm.getMethodAnnotation(RoleRequired.class);
                // Vérifie annotation sur la classe
                RoleRequired roleClass = hm.getBeanType().getAnnotation(RoleRequired.class);

                RoleRequired role = (roleMethod != null) ? roleMethod : roleClass;
                if (role != null) {
                    for (UserRole allowed : role.value()) {
                        if (allowed == userRole) return false; // autorisé
                    }
                    return true; // rôle non autorisé
                }
            }
        } catch (Exception e) {
            // En cas d'erreur, on autorise par défaut pour éviter blocage
            return false;
        }

        return false; // pas d’annotation → accès autorisé
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
