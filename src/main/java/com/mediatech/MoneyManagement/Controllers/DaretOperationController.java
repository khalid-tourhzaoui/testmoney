package com.mediatech.MoneyManagement.Controllers;

import java.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mediatech.MoneyManagement.Models.*;
import com.mediatech.MoneyManagement.Repositories.DaretOperationRepository;
import com.mediatech.MoneyManagement.Services.DaretOperationService;
import com.mediatech.MoneyManagement.Services.UserService;

@Controller
public class DaretOperationController {
	@Autowired
    private DaretOperationService daretOperationService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
    private DaretOperationRepository daretOperationRepository;
	
	
	

	@GetMapping("/liste-des-tontines")
	public String listeOffres(@RequestParam(name = "status", defaultValue = "All") String status,Model model,
	    @AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes) {
	    try {
	        // Vérifier si l'utilisateur actuel est null, ce qui indiquerait une session terminée
	        if (userDetails == null) {
	            // Rediriger vers la page de déconnexion
	            return "redirect:/logout";
	        }
	        System.out.println("liste-des-tontines");

	        // Récupérer l'utilisateur actuel
	        User currentUser = userService.findByEmail(userDetails.getUsername());
	        List<DaretOperation> userDaretOperations;

	        // Vérifier le statut et récupérer les opérations correspondantes
	        if (status.equals("All")) {
	            userDaretOperations = daretOperationService.findByAdminOffre(currentUser);
	        } else {
	            userDaretOperations = daretOperationService.findByAdminOffreAndStatus(currentUser, status);
	        }

	        // Compter le nombre d'opérations dans différents états
	        long inProgressCount = daretOperationRepository.countByStatusAndAdminOffre("Progress", currentUser);
	        long pendingCount = daretOperationRepository.countByStatusAndAdminOffre("Pending", currentUser);
	        long closedCount = daretOperationRepository.countByStatusAndAdminOffre("Closed", currentUser);
	        long totalOffersCount = daretOperationRepository.countByAdminOffre(currentUser);

	        // Ajouter les attributs au modèle pour affichage dans la vue
	        model.addAttribute("user", currentUser)
	            .addAttribute("userDaretOperations", userDaretOperations)
	            .addAttribute("inProgressCount", inProgressCount)
	            .addAttribute("pendingCount", pendingCount)
	            .addAttribute("closedCount", closedCount)
	            .addAttribute("totalOffersCount", totalOffersCount)
	            .addAttribute("selectedStatus", status)
	             .addAttribute("url","liste-des-tontines")

	            .addAttribute("pageTitle", "DARET-ADMIN LISTE DES OFFRES");

	        // Renvoyer le nom de la vue pour afficher la liste des offres
	        return "Admin/liste-tontine";
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
	        return "redirect:/logout";
	    }
	}


	//-------------------------------------------------------------------------------------------------------------------------------------------
	@GetMapping("/ajouter-tontine")
	public String AddOffre(Model model, @AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes) {
	    try {
	        // Vérifier si userDetails est null
	        if (userDetails == null) {
	            // Rediriger vers la page de déconnexion ou toute autre page appropriée
	            return "redirect:/logout";
	        }
	        // Obtenir les détails de l'utilisateur actuellement authentifié
	        User currentUser = userService.findByEmail(userDetails.getUsername());
	        // Vérifier si currentUser est null, indiquant un problème de session
	        if (currentUser == null) {
	            // Rediriger vers la page de déconnexion ou toute autre page appropriée
	            return "redirect:/logout";
	        }
	     
	        // Ajouter les détails de l'utilisateur authentifié au modèle
	        model.addAttribute("user", currentUser);
	        // Créer une nouvelle instance de DaretOperation et l'ajouter au modèle
	        DaretOperation daretOperation = new DaretOperation();
	        model.addAttribute("daretOperation", daretOperation)
	             .addAttribute("pageTitle", "DARET-ADMIN AJOUTER UNE TONTINE");
	        // Renvoyer le nom de la vue pour le formulaire d'ajout d'offre
	        return "Admin/ajouter-tontine";
	    } catch (Exception e) {
	        // Journaliser ou gérer l'exception selon les besoins
	        System.out.println("Erreur au niveau ajouter offre ====> " + e.getMessage());
	        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
	        return "redirect:/logout";
	    }
	}

	//------------------------------------------------------------------------------------------------------------------------------------------
	@PostMapping("/ajouter-tontine")
	public String saveOffer(@ModelAttribute("daretOperation") DaretOperation daretOperation,@AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes redirectAttributes,Model model) {
	    try {
	        // Récupérez l'utilisateur actuellement authentifié
	        User currentUser = userService.findByEmail(userDetails.getUsername());
	        // Vérifier si currentUser est null, indiquant un problème de session
	        if (currentUser == null) {
	            // Rediriger vers la page de déconnexion ou toute autre page appropriée
	            return "redirect:/logout";
	        }
	        // Validation manuelle
	        if (daretOperation.getDesignation() != null
                    && (daretOperation.getDesignation().length() < 5 || daretOperation.getDesignation().length() > 15)) {
                // Ajouter un message d'erreur pour la longueur de la désignation
                model.addAttribute("errorMessage", "La désignation doit avoir entre 5 et 15 caractères.");
                model.addAttribute("user", currentUser);
                return "Admin/ajouter-tontine";
            }
	        if (daretOperation.getMontantParPeriode() <= 0) {
	            // Ajoutez un message d'erreur pour le montant par période
	            model.addAttribute("errorMessage", "Le montant par période doit être un nombre positif");
	            model.addAttribute("user", currentUser);
	            return "Admin/ajouter-tontine"; // Directly return the template without redirect
	        }
	        if (!Arrays.asList("Quotidienne", "Hebdomadaire", "Mensuelle").contains(daretOperation.getTypePeriode())) {
	            // Ajoutez un message d'erreur pour le type de période
	            model.addAttribute("errorMessage", "Le type de période doit être Quotidienne, Hebdomadaire ou Mensuelle");
	            model.addAttribute("user", currentUser);
	            return "Admin/ajouter-tontine"; // Directly return the template without redirect
	        }
	        if (daretOperation.getNombreParticipant() < 3) {
	            // Ajoutez un message d'erreur pour le nombre de participants
	            model.addAttribute("errorMessage", "Le nombre de participants doit être d'au moins 3");
	            model.addAttribute("user", currentUser);
	            return "Admin/ajouter-tontine"; // Directly return the template without redirect
	        }
	        // Configurez les propriétés de l'opération
	        daretOperation.setAdminOffre(currentUser);
	        daretOperation.setStatus("Pending");
	        daretOperation.setDateDebut(null);
	        daretOperation.setDateFin(null);
	        daretOperation.setTourDeRole(1);
	        // Enregistrez l'opération
	        daretOperationService.save(daretOperation);
	        // Redirigez l'utilisateur vers la page de liste des offres avec un message de succès
	        /*model.addAttribute("successMessage", "L'opération a été ajoutée avec succès.")
	        .addAttribute("user", currentUser);*/
	        redirectAttributes.addFlashAttribute("successMessage", "La tontine a été ajoutée avec succès.");
	        return "redirect:/liste-des-tontines";
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
	        return "redirect:/logout";
	    }
	}




	/*-------------------------------------------------------------------------------------------------------------------------------------------*/
	@GetMapping("/modifier-tontine/{operationId}")
	public String showUpdateForm(@PathVariable Long operationId, Model model,
			RedirectAttributes redirectAttributes,@AuthenticationPrincipal UserDetails userDetails) {
	    try {
	        // Récupérer l'utilisateur actuel
	        User currentUser = userService.findByEmail(userDetails.getUsername());

	        // Vérifier si currentUser est null, ce qui indique un problème de session
	        if (currentUser == null) {
	            // Rediriger vers la page de déconnexion ou toute autre page appropriée
	            return "redirect:/logout";
	        }

	        // Ajouter les détails de l'utilisateur authentifié au modèle
	        model.addAttribute("user", currentUser);

	        // Récupérer la DaretOperation par ID
	        DaretOperation daretOperation = daretOperationService.findById(operationId);

	        // Effectuer une vérification d'autorisation ici si nécessaire

	        // Si la DaretOperation n'est pas en cours, afficher le formulaire de modification
	        model.addAttribute("daretOperation", daretOperation)
	             .addAttribute("pageTitle", "DARET-ADMIN MODIFIER UNE TONTINE");

	        // Retourner le nom de la vue pour le formulaire de modification d'offre
	        return "Admin/modifier-tontine";
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
	        return "redirect:/logout";
	    }
	}



	    //-------------------------------------------------------------------------------------------------------------------------------------------
	@PostMapping("/modifier-tontine/{operationId}")
	public String updateOffer(@PathVariable Long operationId,
	                          @ModelAttribute("daretOperation") DaretOperation updatedDaretOperation,
	                          @AuthenticationPrincipal UserDetails userDetails, Model model,RedirectAttributes redirectAttributes) {
	    try {
	        // Récupérer l'utilisateur actuel
	        User currentUser = userService.findByEmail(userDetails.getUsername());

	        // Récupérer l'opération existante par ID
	        DaretOperation existingDaretOperation = daretOperationService.findById(operationId);

	        // Vérifier si l'opération existante et l'utilisateur actuel ne sont pas null
	        if (existingDaretOperation != null && currentUser != null) {

	            // Validation manuelle
	            if (updatedDaretOperation.getMontantParPeriode() <= 0) {
	                // Ajouter un message d'erreur pour le montant par période
	            	redirectAttributes.addFlashAttribute("errorMessage", "Le montant par période doit être un nombre positif");
	                //model.addAttribute("user", currentUser);
	                return "redirect:/modifier-tontine/" + operationId;
	            }
	            if (updatedDaretOperation.getDesignation() != null
	                    && (updatedDaretOperation.getDesignation().length() < 5 || updatedDaretOperation.getDesignation().length() > 15)) {
	                // Ajouter un message d'erreur pour la longueur de la désignation
	            	redirectAttributes.addFlashAttribute("errorMessage", "La désignation doit avoir entre 5 et 15 caractères.");
	                //model.addAttribute("user", currentUser);
	                return "redirect:/modifier-tontine/" + operationId;
	            }
	            if (!Arrays.asList("Quotidienne", "Mensuelle", "Hebdomadaire").contains(updatedDaretOperation.getTypePeriode())) {
	                // Ajouter un message d'erreur pour le type de période
	            	redirectAttributes.addFlashAttribute("errorMessage", "Le type de période doit être Quotidienne, Hebdomadaire ou Mensuelle");
	                //model.addAttribute("user", currentUser);
	                return "redirect:/modifier-tontine/" + operationId;
	            }
	            if (updatedDaretOperation.getNombreParticipant() < 3) {
	                // Ajouter un message d'erreur pour le nombre de participants
	            	redirectAttributes.addFlashAttribute("errorMessage", "Le nombre de participants doit être d'au moins 3");
	                //model.addAttribute("user", currentUser);
	                return "redirect:/modifier-tontine/" + operationId;
	            }

	            // Mettre à jour les propriétés de l'opération existante avec les nouvelles valeurs
	            existingDaretOperation.setDesignation(updatedDaretOperation.getDesignation());
	            existingDaretOperation.setNombreParticipant(updatedDaretOperation.getNombreParticipant());
	            existingDaretOperation.setMontantParPeriode(updatedDaretOperation.getMontantParPeriode());
	            existingDaretOperation.setTypePeriode(updatedDaretOperation.getTypePeriode());

	            // Configurer les propriétés supplémentaires de l'opération
	            existingDaretOperation.setAdminOffre(currentUser);
	            existingDaretOperation.setStatus("Pending");

	            // Enregistrer l'opération mise à jour
	            daretOperationService.save(existingDaretOperation);
	        }
        	redirectAttributes.addFlashAttribute("successMessage", "La tontine a été modifiée avec succès");
	        return "redirect:/liste-des-tontines";
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
	        return "redirect:/logout";
	    }
	}





	//--------------------------------------------------------------------------------------------------------------------------------------------
	@GetMapping("/details-tontine/{operationId}")
	public String showOfferDetails(@PathVariable Long operationId,@AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes redirectAttributes,Model model){
	    try {

	        // Récupérer l'utilisateur actuel et les détails de la demande
	        User currentUser = userService.findByEmail(userDetails.getUsername());
	        // Vérifier si l'utilisateur actuel est null, ce qui indiquerait une session terminée
	        if (currentUser == null) {
	            // Rediriger vers la page de déconnexion
	            return "redirect:/logout";
	        }
	        // Récupérer l'opération par ID et les participants associés
	        DaretOperation daretOperation = daretOperationService.findById(operationId);
	        List<DaretParticipant> participants = daretOperation.getDaretParticipants();

	        if (!participants.isEmpty() && daretOperation.getStatus().equalsIgnoreCase("Progress")) {
	            // Obtenez le dernier participant dans la liste
	            DaretParticipant avantParticipant = participants.get(participants.size() - 2);
	            DaretParticipant dernierParticipant = participants.get(participants.size() - 1);
	            if(dernierParticipant.getDatePaiement()==null) {
	            	/*System.out.println("avant dernier : "+avantParticipant.getDatePaiement());
	  	            System.out.println("le dernier : "+dernierParticipant.getDatePaiement());*/
	  	            dernierParticipant.setDatePaiement(avantParticipant.getDatePaiement());
	  	            daretOperation.setDaretParticipants(participants);
	  	            /*for (DaretParticipant daretParticipant : participants) {
						System.out.println("date paiement : "+daretParticipant.getDatePaiement());
					}*/
	            }
	          
	        }
	        // Ajouter les attributs au modèle pour l'affichage dans la vue
	        model.addAttribute("user", currentUser)
	            .addAttribute("daretOperation", daretOperation)
	            .addAttribute("participants", participants)
	            .addAttribute("pageTitle", "DARET-ADMIN DÉTAILS DE TONTINE");

	        // Renvoyer le nom de la vue pour afficher les détails de l'offre
	        return "Admin/details-tontine";
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
	        return "redirect:/logout"; 
	    }
	}




	//--------------------------------------------------------------------------------------------------------------------------------------------
	@PostMapping("/supprimer-tontine")
	public String deleteDaret(@RequestParam Long operationId, @AuthenticationPrincipal UserDetails userDetails,Model model,
			RedirectAttributes redirectAttributes) {
	    try {
	        // Récupérer la DaretOperation par ID
	        DaretOperation daretOperation = daretOperationService.findById(operationId);

	        // Récupérer l'utilisateur actuel
	        User currentUser = userService.findByEmail(userDetails.getUsername());

	        // Vérifier si l'utilisateur actuel est le créateur
	        if (("CREATEUR".equals(currentUser.getRole())) || ("ADMIN".equalsIgnoreCase(currentUser.getRole()))){
	            // Si le créateur veut supprimer et la tontine est en attente, alors supprime
	            if (!"Progress".equals(daretOperation.getStatus())) {
	                // Implémenter votre méthode de service pour supprimer la DaretOperation par ID
	                daretOperationService.deleteDaretById(operationId);
	            } else {
	            	model.addAttribute("errorMessage", "Vous n'êtes pas autorisé à supprimer une tontine en cours.")
	        		 .addAttribute("user",currentUser);
	            	return "liste-tontine";
	            }
	        }
        	redirectAttributes.addFlashAttribute("successMessage", "La tontine a été supprimée avec succès");
	        return "redirect:/liste-des-tontines";
	    } catch (Exception e) {
	    	redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
	        return "redirect:/logout";
	    }
	}



		//--------------------------------------------------------------------------------------------------------------------------------------------
	    @GetMapping("/liste-offres-pending")
	    public String listPendingOffers(Model model, @AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes) {
	        try {
	            // Vérifier si l'utilisateur actuel est null, ce qui pourrait indiquer une session expirée
	            if (userDetails == null) {
	                // Rediriger vers la page de déconnexion
	                return "redirect:/logout";
	            }

	            // Récupérer les détails de l'utilisateur actuellement authentifié
	            User currentUser = userService.findByEmail(userDetails.getUsername());

	            // Récupérer la liste des offres en attente ("Pending") pour tous les administrateurs
	            List<DaretOperation> pendingOffers = daretOperationService.findPendingOffers();

	            // Ajouter les détails de l'utilisateur authentifié et la liste des offres en attente au modèle
	            model.addAttribute("user", currentUser)
	                 .addAttribute("pendingOffers", pendingOffers)
	                 .addAttribute("pageTitle", "DARET-ADMIN LISTE DES OFFRES EN ATTENTE");

	            // Renvoyer le nom de la vue pour afficher la liste des offres en attente
	            return "Admin/liste-offres-pending";
	        } catch (Exception ex) {
	        	redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
	            return "redirect:/logout"; 
	        }
	    }

	   
	  


    
}
