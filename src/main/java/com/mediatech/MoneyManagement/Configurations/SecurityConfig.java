
package com.mediatech.MoneyManagement.Configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mediatech.MoneyManagement.Services.CustomSuccessHandler;
import com.mediatech.MoneyManagement.Services.CustomUserDetailsService;

@Configuration // Indique que cette classe est une configuration Spring
@EnableWebSecurity // Active la configuration de sécurité Web de Spring Security
public class SecurityConfig {

    @Autowired
    CustomSuccessHandler customSuccessHandler; // Injection de dépendances pour le gestionnaire de succès personnalisé

    @Autowired
    CustomUserDetailsService customUserDetailsService; // Injection de dépendances pour le service utilisateur personnalisé

    // Définir le gestionnaire d'encodeur de mot de passe
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configurer les filtres de sécurité pour les requêtes HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    	// Désactivation de la protection CSRF (Cross-Site Request Forgery)
    	http.csrf(c -> c.disable())

            .authorizeHttpRequests(request -> request
                // Autorisations basées sur les rôles
                .requestMatchers("/admin-dashboard","/liste-utilisateurs","/liste-des-toutes-les-tontines")
                    .hasAuthority("ADMIN")
                .requestMatchers("/createur-dashboard")
                    .hasAuthority("CREATEUR")
                .requestMatchers("/registration","/liste-des-participations","/liste-offres-pending","update-info","password-request",
                		"/supprimer-tontine","/profile","reset-password","/liste-des-tontines","/ajouter-une-tontine","/**")
                    .permitAll()
                .anyRequest().authenticated())

            .formLogin(form -> form
            	    // Page personnalisée de connexion (par défaut : "/login")
            	    .loginPage("/login")
            	    
            	    // URL du point de traitement de la connexion (par défaut : "/login")
            	    .loginProcessingUrl("/login")
            	    // Gestionnaire personnalisé pour le succès de la connexion
            	    .successHandler(customSuccessHandler)
            	    
            	    // Autorise l'accès à la page de connexion pour tous les utilisateurs (non authentifiés)
            	    .permitAll())

            .logout(form -> form
            	    // Invalider la session HTTP existante lors de la déconnexion
            	    .invalidateHttpSession(true)
            	    
            	    // Effacer toutes les informations d'authentification lors de la déconnexion
            	    .clearAuthentication(true)
            	    
            	    // Spécifier le chemin de la requête de déconnexion (par défaut : "/logout")
            	    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            	    
            	    // URL vers laquelle rediriger après une déconnexion réussie
            	    .logoutSuccessUrl("/login?logout")
            	    
            	    // Autoriser toutes les requêtes de déconnexion sans authentification préalable
            	    .permitAll());


        http.sessionManagement(management -> management
        	    // Configuration de la gestion de session
        	    .sessionFixation()
        	        .migrateSession()
        	    // URL à rediriger en cas de session expirée
        	    .invalidSessionUrl("/login?expired")
        	    // Limiter à une seule session par utilisateur
        	    .maximumSessions(1)
        	    // URL à rediriger en cas de session expirée (pour les sessions additionnelles)
        	    .expiredUrl("/login?expired")
        	    // Empêcher la connexion si le nombre maximal de sessions est atteint
        	    .maxSessionsPreventsLogin(true));

        return http.build();
    }

 // Configuration de l'authentification : injection des dépendances, définition du service utilisateur et du gestionnaire de mot de passe.
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }
}

