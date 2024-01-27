package com.mediatech.MoneyManagement.Services;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mediatech.MoneyManagement.Models.User;


//Cette classe implémente l'interface UserDetails de Spring Security, fournissant les détails de l'utilisateur nécessaires pour l'authentification.
public class CustomUserDetail implements UserDetails {

 private User user; // Représente l'objet User associé à l'utilisateur authentifié.

 // Constructeur prenant un objet User pour initialiser l'instance CustomUserDetail.
 public CustomUserDetail(User user) {
     this.user = user;
 }

//Cette méthode fait partie de l'interface UserDetails de Spring Security et doit être implémentée.
//Elle renvoie une collection d'autorités (rôles) attribuées à l'utilisateur dans le contexte de Spring Security.
 @Override
public Collection<? extends GrantedAuthority> getAuthorities() {
  // La méthode List.of() crée une liste immuable contenant un élément.
  // Dans ce cas, elle crée une liste contenant une seule autorité, obtenue en appelant la méthode getRole() de l'objet User.
  return List.of(() -> user.getRole());
}

 // Méthodes pour récupérer divers attributs de l'objet User.
 public String getNom() {
     return user.getNom();
 }

 public String getPrenom() {
     return user.getPrenom();
 }

 public String getRole() {
     return user.getRole();
 }

 public String getEmail() {
     return user.getEmail();
 }

 public String getGender() {
     return user.getGender();
 }

 // Méthodes nécessaires pour UserDetails, renvoyant le nom d'utilisateur, le mot de passe et des informations sur la validité du compte.
 @Override
 public String getPassword() {
     return user.getPassword();
 }

 @Override
 public String getUsername() {
     return user.getEmail();
 }

 @Override
 public boolean isAccountNonExpired() {
     return true;
 }

 @Override
 public boolean isAccountNonLocked() {
     return true;
 }

 @Override
 public boolean isCredentialsNonExpired() {
     return true;
 }

 @Override
 public boolean isEnabled() {
     return true;
 }

}
