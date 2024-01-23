package com.mediatech.MoneyManagement.Controllers;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.mediatech.MoneyManagement.Models.User;
import com.mediatech.MoneyManagement.Services.DaretOperationService;
import com.mediatech.MoneyManagement.Services.UserService;


@Controller
public class ProfileController {
	@Autowired
    private DaretOperationService daretOperationService;
    @Autowired
    private UserService userService;
    /*---------------------------------------------------------------------------------------------------------------*/
    @GetMapping("/profile")
    public String userProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            // userDetails contient des informations sur l'utilisateur authentifié
            User currentUser = userService.findByEmail(userDetails.getUsername());

            // Vérifiez si currentUser est null
            if (currentUser == null) {
                // Redirige vers la page de déconnexion si l'utilisateur n'est pas authentifié
                return "redirect:/logout";
            }

            // Ajoutez l'objet utilisateur au modèle pour l'afficher dans la vue
            model.addAttribute("user", currentUser)
                 .addAttribute("pageTitle", "DARET-ADMIN PROFILE");

            return "profile";
        } catch (Exception e) {
            // Journalisez l'exception (facultatif)
            e.printStackTrace();

            // Redirige vers la page de déconnexion en cas d'exception
            return "redirect:/logout";
        }
    }


    /*---------------------------------------------------------------------------------------------------------------*/
    @PostMapping("/update-info")
    public String updateInfo(@ModelAttribute("user") User updatedUser, Model model) {
        try {
            // Récupération de l'utilisateur d'origine depuis la base de données
            User originalUser = userService.findById(updatedUser.getId());

            // Vérifiez si originalUser est null
            if (originalUser == null) {
                // Redirige vers la page de déconnexion si l'utilisateur n'est pas authentifié
                return "redirect:/logout";
            }

            // Vérification des changements d'e-mail et de CIN
            if (!originalUser.getEmail().equals(updatedUser.getEmail()) &&
                    userService.existsByEmail(updatedUser.getEmail())) {
                model.addAttribute("ErrorMessage", "L'adresse e-mail existe déjà. Veuillez choisir une adresse e-mail différente.");
                return "profile";
            } else if (!originalUser.getCin().equals(updatedUser.getCin()) &&
                    userService.existsByCin(updatedUser.getCin())) {
                model.addAttribute("ErrorMessage", "Le CIN existe déjà. Veuillez choisir un CIN différent.");
                return "profile";
            } else if (!Arrays.asList("male", "female").contains(updatedUser.getGender())) {
                model.addAttribute("ErrorMessage", "Genre invalide. Veuillez choisir 'homme' ou 'femme'.");
                return "profile";
            }

            // Mise à jour des informations de l'utilisateur
            userService.updateUserInfo(updatedUser);

            // Attribution d'un message de succès
            model.addAttribute("SuccessMessage", "Les informations de l'utilisateur ont été mises à jour avec succès !");
            return "profile";
        } catch (Exception e) {
            // Gestion de l'exception
            e.printStackTrace(); 
            // Redirection vers la page de profil avec un message d'erreur
            model.addAttribute("ErrorMessage", "Une erreur s'est produite lors de la mise à jour des informations de l'utilisateur.");
            return "profile";
        }
    }




    /*---------------------------------------------------------------------------------------------------------------*/
    @PostMapping("/update-password")
    public String updatePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        try {
            // userDetails contient des informations sur l'utilisateur authentifié
            User currentUser = userService.findByEmail(userDetails.getUsername());
            // Vérifiez si currentUser est null
            if (currentUser == null) {
                // Redirige vers la page de déconnexion si l'utilisateur n'est pas authentifié
                return "redirect:/logout";
            }
            // Récupérer l'e-mail de l'utilisateur actuellement authentifié
            String userEmail = currentUser.getEmail();
            // Vérifier si le mot de passe actuel est correct
            if (!userService.isCorrectPassword(userEmail, currentPassword)) {
                // Ajouter un message d'erreur pour mot de passe actuel incorrect
                model.addAttribute("ErrorMessage", "Mot de passe actuel incorrect.")
                	  .addAttribute("user",currentUser);
                return "profile";
            }

            // Vérifier si le nouveau mot de passe et le mot de passe de confirmation correspondent
            if (!newPassword.equals(confirmPassword)) {
                // Ajouter un message d'erreur pour la non-correspondance des mots de passe
                model.addAttribute("ErrorMessage", "Le nouveau mot de passe et le mot de passe de confirmation ne correspondent pas.")
                .addAttribute("user",currentUser);
                return "profile";
            }

            // Vérifier si le nouveau mot de passe est différent de l'ancien mot de passe
            if (userService.isCorrectPassword(userEmail, newPassword)) {
                // Ajouter un message d'erreur pour le nouveau mot de passe identique à l'ancien
                model.addAttribute("ErrorMessage", "Le nouveau mot de passe doit être différent de l'actuel.")
                .addAttribute("user",currentUser);
                return "profile";
            }

            // Mettre à jour le mot de passe de l'utilisateur
            userService.updateUserPassword(userEmail, currentPassword, newPassword);

            // Ajouter un message de succès
            model.addAttribute("SuccessMessage", "Mot de passe mis à jour avec succès !")
            .addAttribute("user",currentUser);
            return "profile";
        } catch (Exception e) {
            // Gérer d'autres exceptions
            e.printStackTrace();
            return "redirect:/logout";
        }
    }


    /*---------------------------------------------------------------*/
    @PostMapping("/delete-account")
    public String deleteAccount(@ModelAttribute("user") User user, 
                                @RequestParam("password") String password,
                                Model model) {
        try {
            // Récupérer l'utilisateur d'origine depuis la base de données
            User existingUser = userService.findByEmail(user.getEmail());

            // Vérifier si l'utilisateur existe
            if (existingUser == null) {
                model.addAttribute("ErrorMessage", "Utilisateur non trouvé. La suppression du compte a échoué.");
                return "redirect:/logout";
            }

            // Vérifier si le mot de passe saisi est correct
            if (!userService.isCorrectPassword(existingUser.getEmail(), password)) {
                model.addAttribute("ErrorMessage", "Mot de passe incorrect. La suppression du compte a échoué.")
                .addAttribute("user",existingUser);
                return "profile";
            }

            // Vérifier si l'utilisateur a créé des Daret non terminées
            if (daretOperationService.isUserCreatedUnfinishedDarets(existingUser)) {
                model.addAttribute("ErrorMessage", "L'utilisateur a créé des Daret qui ne sont pas encore terminées. Veuillez terminer ces Daret avant de supprimer le compte.")
                .addAttribute("user",existingUser);
                return "profile";
            }

            // Vérifier si l'utilisateur est participant à des Daret non terminées
            if (daretOperationService.isUserParticipantInUnfinishedDarets(existingUser)) {
                model.addAttribute("ErrorMessage", "L'utilisateur est participant à des Daret qui ne sont pas encore terminées. Veuillez quitter ces Daret avant de supprimer le compte.")
                .addAttribute("user",existingUser);
                return "profile";
            }

            // Effectuer la suppression du compte
            userService.deleteUser(existingUser.getEmail(), password);

            // Attribuer un message de succès
            model.addAttribute("SuccessMessage", "Compte supprimé avec succès!");

            // Rediriger vers la page de déconnexion
            return "redirect:/logout";
        } catch (Exception e) {
            // Gestion des exceptions
            e.printStackTrace();
            model.addAttribute("ErrorMessage", "Une erreur s'est produite lors de la suppression du compte.");
            return "profile";
        }
    }




}