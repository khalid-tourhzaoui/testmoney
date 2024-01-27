package com.mediatech.MoneyManagement.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mediatech.MoneyManagement.Models.ForgotPasswordToken;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken, Long> {

    // Méthode pour trouver un jeton de réinitialisation de mot de passe par son token
    ForgotPasswordToken findByToken(String token);
}
