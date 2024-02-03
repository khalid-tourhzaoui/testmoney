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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public String userProfile(@AuthenticationPrincipal UserDetails userDetails, Model model,RedirectAttributes redirectAttributes) {
		try {
			// userDetails contient des informations sur l'utilisateur authentifié
			User currentUser = userService.findByEmail(userDetails.getUsername());

			// Vérifiez si currentUser est null
			if (currentUser == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
				// Redirige vers la page de déconnexion si l'utilisateur n'est pas authentifié
				return "redirect:/logout";
			}

			// Ajoutez l'objet utilisateur au modèle pour l'afficher dans la vue
			model.addAttribute("user", currentUser)
					.addAttribute("pageTitle", "DARET-ADMIN PROFILE");

			return "profile";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
			return "redirect:/logout";
		}
	}

	/*---------------------------------------------------------------------------------------------------------------*/
	@PostMapping("/update-info")
	public String updateInfo(@ModelAttribute("user") User updatedUser,RedirectAttributes redirectAttributes) {
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
				redirectAttributes.addFlashAttribute("errorMessage","L'adresse e-mail existe déjà. Veuillez choisir une adresse e-mail différente.");
				return "redirect:/profile";
			} else if (!originalUser.getCin().equals(updatedUser.getCin()) &&
					userService.existsByCin(updatedUser.getCin())) {
				redirectAttributes.addFlashAttribute("errorMessage","Le CIN existe déjà. Veuillez choisir un CIN différent.");
				return "redirect:/profile";
			} else if (!Arrays.asList("femme", "homme").contains(updatedUser.getGender())) {
				redirectAttributes.addFlashAttribute("ErrorMessage", "Genre invalide. Veuillez choisir 'homme' ou 'femme'.");
				return "redirect:/profile";
			}

			// Mise à jour des informations de l'utilisateur
			userService.updateUserInfo(updatedUser);

			// Attribution d'un message de succès
			redirectAttributes.addFlashAttribute("successMessage", "Les informations de l'utilisateur ont été mises à jour avec succès !");
			return "redirect:/profile";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage","Une erreur s'est produite lors de la mise à jour des informations de l'utilisateur.");
			return "redirect:/logout";
		}
	}

	/*---------------------------------------------------------------------------------------------------------------*/
	@PostMapping("/update-password")
	public String updatePassword(
			@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword,
			@AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes redirectAttributes) {

		try {
			// userDetails contient des informations sur l'utilisateur authentifié
			User currentUser = userService.findByEmail(userDetails.getUsername());
			// Vérifiez si currentUser est null
			if (currentUser == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
				// Redirige vers la page de déconnexion si l'utilisateur n'est pas authentifié
				return "redirect:/logout";
			}
			// Récupérer l'e-mail de l'utilisateur actuellement authentifié
			String userEmail = currentUser.getEmail();
			// Vérifier si le mot de passe actuel est correct
			if (!userService.isCorrectPassword(userEmail, currentPassword)) {
				// Ajouter un message d'erreur pour mot de passe actuel incorrect
				redirectAttributes.addFlashAttribute("errorMessage", "Mot de passe actuel incorrect.");
				return "redirect:/profile";
			}

			// Vérifier si le nouveau mot de passe et le mot de passe de confirmation
			// correspondent
			if (!newPassword.equals(confirmPassword)) {
				// Ajouter un message d'erreur pour la non-correspondance des mots de passe
				redirectAttributes.addFlashAttribute("errorMessage","Le nouveau mot de passe et le mot de passe de confirmation ne correspondent pas.");
				return "redirect:/profile";
			}
			if(newPassword.length()<8 || confirmPassword.length()<8 ) {
				redirectAttributes.addFlashAttribute("errorMessage","Le mot de passe doit contenir au moins 8 caractères.");
				return "redirect:/profile";
			}

			// Vérifier si le nouveau mot de passe est différent de l'ancien mot de passe
			if (userService.isCorrectPassword(userEmail, newPassword)) {
				// Ajouter un message d'erreur pour le nouveau mot de passe identique à l'ancien
				redirectAttributes.addFlashAttribute("errorMessage", "Le nouveau mot de passe doit être différent de l'actuel.");
				return "redirect:/profile";
			}

			// Mettre à jour le mot de passe de l'utilisateur
			userService.updateUserPassword(userEmail, currentPassword, newPassword);

			// Ajouter un message de succès
			redirectAttributes.addFlashAttribute("successMessage", "Mot de passe mis à jour avec succès !");
			return "redirect:/profile";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage","Une erreur s'est produite lors de la mise à jour de mot de passe d'utilisateur.");
			return "redirect:/logout";
		}
	}

	/*---------------------------------------------------------------*/
	@PostMapping("/delete-account")
	public String deleteAccount(@ModelAttribute("user") User user,
			@RequestParam("password") String password,
			RedirectAttributes redirectAttributes) {
		try {
			// Récupérer l'utilisateur d'origine depuis la base de données
			User existingUser = userService.findByEmail(user.getEmail());

			// Vérifier si l'utilisateur existe
			if (existingUser == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur non trouvé. La suppression du compte a échoué.");
				return "redirect:/logout";
			}

			// Vérifier si le mot de passe saisi est correct
			if (!userService.isCorrectPassword(existingUser.getEmail(), password)) {
				redirectAttributes.addFlashAttribute("errorMessage", "Mot de passe incorrect. La suppression du compte a échoué.");
				return "redirect:/profile";
			}

			// Vérifier si l'utilisateur a créé des Daret non terminées
			if (daretOperationService.isUserCreatedUnfinishedDarets(existingUser)) {
				redirectAttributes.addFlashAttribute("errorMessage","L'utilisateur a créé des Daret qui ne sont pas encore terminées. Veuillez terminer ces Daret avant de supprimer le compte.");
				return "redirect:/profile";
			}

			// Vérifier si l'utilisateur est participant à des Daret non terminées
			if (daretOperationService.isUserParticipantInUnfinishedDarets(existingUser)) {
				redirectAttributes.addFlashAttribute("errorMessage","L'utilisateur est participant à des Daret qui ne sont pas encore terminées. Veuillez quitter ces Daret avant de supprimer le compte.");
				return "redirect:/profile";
			}

			// Effectuer la suppression du compte
			userService.deleteUser(existingUser.getEmail(), password);

			// Attribuer un message de succès
			redirectAttributes.addFlashAttribute("successMessage", "Compte supprimé avec succès!");

			// Rediriger vers la page de déconnexion
			return "redirect:/logout";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors de la suppression du compte.");
			return "redirect:/profile";
		}
	}

}