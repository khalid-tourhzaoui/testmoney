package com.mediatech.MoneyManagement.Services;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mediatech.MoneyManagement.Models.User;
import com.mediatech.MoneyManagement.Repositories.UserRepository;




@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	 @Autowired
	 private UserRepository userRepository;

	// Cette méthode est appelée lorsqu'un utilisateur tente de s'authentifier
	    @Override
	    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	        // Utilise le UserRepository pour rechercher un utilisateur dans la base de données en fonction de son adresse e-mail
	        User user = userRepository.findByEmail(email);

	        // Vérifie si l'utilisateur existe. S'il n'existe pas, lance une exception UsernameNotFoundException.
	        if (user == null) {
	            throw new UsernameNotFoundException("user not found");
	        }

	        // Crée et retourne un objet CustomUserDetail (une implémentation de l'interface UserDetails) en utilisant l'objet User récupéré de la base de données.
	        // Cet objet contient les informations nécessaires pour l'authentification, telles que le nom d'utilisateur, le mot de passe, les rôles, etc.
	        return new CustomUserDetail(user);
	    }
	}
