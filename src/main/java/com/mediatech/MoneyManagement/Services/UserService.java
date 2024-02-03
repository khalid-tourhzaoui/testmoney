package com.mediatech.MoneyManagement.Services;

import java.util.List;

import com.mediatech.MoneyManagement.DTO.UserDto;
import com.mediatech.MoneyManagement.Models.User;

public interface UserService {

    // Méthode pour enregistrer un utilisateur à partir d'un objet UserDto
    User save(UserDto userDto);

    // Méthode pour vérifier l'existence d'un utilisateur par son adresse e-mail
    boolean existsByEmail(String email);

    // Méthode pour vérifier l'existence d'un utilisateur par son numéro d'identification (CIN)
    boolean existsByCin(String cin);

    // Méthode pour trouver un utilisateur par son adresse e-mail
    User findByEmail(String email);

    // Méthode pour faire l'operation d'oublier le mot de passed'un utilisateur
    User save(User user);

    // Méthode pour mettre à jour les informations d'un utilisateur
    void updateUserInfo(User updatedUser);

    // Méthode pour mettre à jour le mot de passe d'un utilisateur
    void updateUserPassword(String userEmail, String oldPassword, String newPassword);

    // Méthode pour supprimer un utilisateur
    void deleteUser(String userEmail, String password);

    // Méthode pour trouver un utilisateur par son identifiant
    User findById(Long userId);

    // Méthode pour vérifier si le mot de passe spécifié est correct pour un utilisateur donné
    boolean isCorrectPassword(String userEmail, String password);

    // Méthode pour récupérer la liste de tous les utilisateurs
    List<User> findAllUsers();


}
