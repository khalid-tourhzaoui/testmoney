package com.mediatech.MoneyManagement.Repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mediatech.MoneyManagement.Models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Méthode pour trouver un utilisateur par son adresse e-mail
    User findByEmail(String email);

    // Méthode pour vérifier si un utilisateur existe déjà avec une adresse e-mail donnée
    boolean existsByEmail(String email);

    // Méthode pour vérifier si un utilisateur existe déjà avec un numéro d'identification (CIN) donné
    boolean existsByCin(String cin);

    // Méthode pour compter le nombre d'utilisateurs ayant un rôle donné
    long countByRole(String role);

    // Méthode pour trouver une liste d'utilisateurs ayant des rôles parmi ceux spécifiés
    List<User> findByRoleIn(List<String> asList);
    
}
