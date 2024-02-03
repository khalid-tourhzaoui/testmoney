package com.mediatech.MoneyManagement.Models;

import java.time.LocalDateTime;


import jakarta.persistence.*;

@Entity
public class ForgotPasswordToken {
	
	// Identifiant unique généré automatiquement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Token associé à la réinitialisation du mot de passe, non nul
    @Column(nullable = false)
    private String token;

    // Relation Many-to-One avec l'entité User, indiquant l'utilisateur associé au token
  
    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    // Date et heure d'expiration du token
    @Column(nullable = false)
    private LocalDateTime expireTime;

    // Indique si le token a été utilisé ou non
    @Column(nullable = false)
    private boolean isUsed;

    // Getters et setters pour tous les champs

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

}
