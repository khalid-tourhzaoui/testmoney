package com.mediatech.MoneyManagement.Services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mediatech.MoneyManagement.DTO.UserDto;
import com.mediatech.MoneyManagement.Models.User;
import com.mediatech.MoneyManagement.Repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User save(UserDto userDto) {
        // Créer un nouvel utilisateur en utilisant les informations du DTO et encoder le mot de passe
        User user = new User(userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()), userDto.getRole(),
                userDto.getNom(), userDto.getPrenom(), userDto.getCin(), userDto.getGender());
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCin(String cin) {
        return userRepository.existsByCin(cin);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateUserInfo(User updatedUser) {
        // Mettre à jour les informations de l'utilisateur
    	System.out.println(updatedUser.getId());
		User existingUser = userRepository.getOne(updatedUser.getId());
        if (existingUser != null) {
            existingUser.setNom(updatedUser.getNom());
            existingUser.setPrenom(updatedUser.getPrenom());
            existingUser.setCin(updatedUser.getCin());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setGender(updatedUser.getGender());
            userRepository.save(existingUser);
        }
    }

    @Override
    public void updateUserPassword(String userEmail, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(userEmail);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            // Encoder et définir le nouveau mot de passe
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            // Gérer le mot de passe incorrect
            throw new IllegalArgumentException("Incorrect old password");
        }
    }

    @Override
    public boolean isCorrectPassword(String userEmail, String password) {
        User user = userRepository.findByEmail(userEmail);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public void deleteUser(String userEmail, String password) {
        User user = userRepository.findByEmail(userEmail);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Implémenter la logique pour supprimer l'utilisateur
            userRepository.delete(user);
        } else {
            // Gérer le mot de passe incorrect
            throw new IllegalArgumentException("Incorrect password");
        }
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<User> findAllUsers() {
        // Récupérer la liste des utilisateurs par leur rôle
        return userRepository.findByRoleIn(Arrays.asList("USER", "CREATEUR"));
    }
}
