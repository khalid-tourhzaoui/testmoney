package com.mediatech.MoneyManagement.Services;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        // récupère les autorités (ou rôles) de l'utilisateur authentifié à partir de l'objet Authentication dans le contexte de Spring Security.
        var authourities = authentication.getAuthorities();
        // Mappe les autorités en noms de rôles et prend le premier (s'il existe).
        var roles = authourities.stream().map(r -> r.getAuthority()).findFirst();
        
        // Redirection en fonction du rôle de l'utilisateur.
        if (roles.orElse("").equals("ADMIN")) {
            // Redirige vers le tableau de bord de l'administrateur.
            response.sendRedirect("/admin-dashboard");
        } else if (roles.orElse("").equals("CREATEUR")) {
            // Redirige vers le tableau de bord du créateur.
            response.sendRedirect("/createur-dashboard");
        } else if (roles.orElse("").equals("USER")) {
            // Redirige vers le tableau de bord de l'utilisateur.
            response.sendRedirect("/user-dashboard");
        } else {
            // Redirige vers la page de connexion en cas de rôle inconnu.
            response.sendRedirect("/login");
        }
    }
}
