package com.mediatech.MoneyManagement.Controllers;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.mediatech.MoneyManagement.Models.*;
import com.mediatech.MoneyManagement.Repositories.DaretOperationRepository;
import com.mediatech.MoneyManagement.Services.DaretOperationService;
import com.mediatech.MoneyManagement.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;

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
	    @AuthenticationPrincipal UserDetails userDetails) {
	    try {
	        // Vérifier si l'utilisateur actuel est null, ce qui indiquerait une session terminée
	        if (userDetails == null) {
	            // Rediriger vers la page de déconnexion
	            return "redirect:/logout";
	        }

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
	            .addAttribute("pageTitle", "DARET-ADMIN LISTE DES OFFRES");

	        // Renvoyer le nom de la vue pour afficher la liste des offres
	        return "Admin/liste-tontine";
	    } catch (Exception e) {
	        // Gérer les exceptions, par exemple, rediriger vers une page d'erreur ou journaliser
	        System.out.println("Erreur lors de la récupération des offres : " + e.getMessage());
	        return "redirect:/liste-des-tontines?error";
	    }
	}


	//-------------------------------------------------------------------------------------------------------------------------------------------
	@GetMapping("/ajouter-tontine")
	public String AddOffre(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
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
	        // Obtenir l'URL actuelle de la requête et l'ajouter au modèle
	        String currentUrl = request.getRequestURL().toString();
	        model.addAttribute("currentUrl", currentUrl);
	        // Ajouter les détails de l'utilisateur authentifié au modèle
	        model.addAttribute("user", currentUser);
	        // Créer une nouvelle instance de DaretOperation et l'ajouter au modèle
	        DaretOperation daretOperation = new DaretOperation();
	        model.addAttribute("daretOperation", daretOperation)
	             .addAttribute("pageTitle", "DARET-ADMIN AJOUTER UNE OFFRE");
	        // Renvoyer le nom de la vue pour le formulaire d'ajout d'offre
	        return "Admin/ajouter-tontine";
	    } catch (Exception e) {
	        // Journaliser ou gérer l'exception selon les besoins
	        System.out.println("Erreur au niveau ajouter offre ====> " + e.getMessage()); // Journaliser la trace de la pile de l'exception
	        // Rediriger vers la page de déconnexion ou toute autre page appropriée
	        return "redirect:/logout";
	    }
	}

	//------------------------------------------------------------------------------------------------------------------------------------------
	@PostMapping("/ajouter-tontine")
	public String saveOffer(@ModelAttribute("daretOperation") DaretOperation daretOperation,@AuthenticationPrincipal UserDetails userDetails,Model model) {
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
	        if (!Arrays.asList("semaine", "mensuelle", "hebdomadaire").contains(daretOperation.getTypePeriode())) {
	            // Ajoutez un message d'erreur pour le type de période
	            model.addAttribute("errorMessage", "Le type de période doit être semaine, mensuelle ou hebdomadaire");
	            model.addAttribute("user", currentUser);
	            return "Admin/ajouter-tontine"; // Directly return the template without redirect
	        }
	        if (daretOperation.getNombreParticipant() < 2) {
	            // Ajoutez un message d'erreur pour le nombre de participants
	            model.addAttribute("errorMessage", "Le nombre de participants doit être d'au moins 2");
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
	        model.addAttribute("successMessage", "L'opération a été ajoutée avec succès.");
	        return "redirect:/liste-des-tontines";
	    } catch (Exception e) {
	        // Gérez l'exception en journalisant ou en redirigeant vers une page d'erreur appropriée
	        model.addAttribute("errorMessage", "Une erreur s'est produite lors de l'ajout de l'opération.");
	        return "Admin/ajouter-tontine"; // Directly return the template without redirect
	    }
	}




	/*-------------------------------------------------------------------------------------------------------------------------------------------*/
	@GetMapping("/modifier-tontine/{operationId}")
	public String showUpdateForm(@PathVariable Long operationId, Model model,
	                             @AuthenticationPrincipal UserDetails userDetails) {
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
	             .addAttribute("pageTitle", "DARET-ADMIN MODIFIER UNE OFFRE");

	        // Retourner le nom de la vue pour le formulaire de modification d'offre
	        return "Admin/modifier-tontine";
	    } catch (Exception e) {
	        // Gérer les exceptions, par exemple, rediriger vers une page de connexion ou afficher une page d'erreur
	        System.out.println("Erreur au niveau de la modification de l'offre ====> " + e.getMessage()); // Journaliser la trace de la pile de l'exception

	        // Rediriger vers la page de déconnexion ou toute autre page appropriée
	        return "redirect:/logout";
	    }
	}



	    //-------------------------------------------------------------------------------------------------------------------------------------------
	@PostMapping("/modifier-tontine/{operationId}")
	public String updateOffer(@PathVariable Long operationId,
	                          @ModelAttribute("daretOperation") DaretOperation updatedDaretOperation,
	                          @AuthenticationPrincipal UserDetails userDetails, Model model) {
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
	                model.addAttribute("errorMessage", "Le montant par période doit être un nombre positif");
	                model.addAttribute("user", currentUser);
	                return "redirect:/modifier-tontine/" + operationId;
	            }
	            if (updatedDaretOperation.getDesignation() != null
	                    && (updatedDaretOperation.getDesignation().length() < 5 || updatedDaretOperation.getDesignation().length() > 15)) {
	                // Ajouter un message d'erreur pour la longueur de la désignation
	                model.addAttribute("errorMessage", "La désignation doit avoir entre 5 et 15 caractères.");
	                model.addAttribute("user", currentUser);
	                return "redirect:/modifier-tontine/" + operationId;
	            }
	            if (!Arrays.asList("semaine", "mensuelle", "hebdomadaire").contains(updatedDaretOperation.getTypePeriode())) {
	                // Ajouter un message d'erreur pour le type de période
	                model.addAttribute("errorMessage", "Le type de période doit être semaine, mensuelle ou hebdomadaire");
	                model.addAttribute("user", currentUser);
	                return "redirect:/modifier-tontine/" + operationId;
	            }
	            if (updatedDaretOperation.getNombreParticipant() < 2) {
	                // Ajouter un message d'erreur pour le nombre de participants
	                model.addAttribute("errorMessage", "Le nombre de participants doit être d'au moins 2");
	                model.addAttribute("user", currentUser);
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

	        return "redirect:/liste-des-tontines";
	    } catch (Exception e) {
	        // Gérer les exceptions, par exemple, rediriger vers une page d'erreur ou journaliser
	        System.out.println("Erreur lors de la modification de l'offre ====> " + e.getMessage()); // Journaliser la trace de la pile de l'exception
	        return "redirect:/liste-des-tontines"; // Ou rediriger vers une autre page d'erreur
	    }
	}





	//--------------------------------------------------------------------------------------------------------------------------------------------
	@GetMapping("/details-tontine/{operationId}")
	public String showOfferDetails(@PathVariable Long operationId,@AuthenticationPrincipal UserDetails userDetails,Model model){
	    try {
	        // Vérifier si l'utilisateur actuel est null, ce qui indiquerait une session terminée
	        if (userDetails == null) {
	            // Rediriger vers la page de déconnexion
	            return "redirect:/logout";
	        }

	        // Récupérer l'utilisateur actuel et les détails de la demande
	        User currentUser = userService.findByEmail(userDetails.getUsername());

	        // Récupérer l'opération par ID et les participants associés
	        DaretOperation daretOperation = daretOperationService.findById(operationId);
	        List<DaretParticipant> participants = daretOperation.getDaretParticipants();

	        // Effectuer une vérification d'autorisation ici si nécessaire

	        // Ajouter les attributs au modèle pour l'affichage dans la vue
	        model.addAttribute("user", currentUser)
	            .addAttribute("daretOperation", daretOperation)
	            .addAttribute("participants", participants)
	            .addAttribute("pageTitle", "DARET-ADMIN DÉTAILS DE L'OFFRE");

	        // Renvoyer le nom de la vue pour afficher les détails de l'offre
	        return "Admin/hara";

	    } catch (Exception e) {
	        // Gérer les exceptions, par exemple, rediriger vers une page d'erreur ou journaliser
	        System.out.println("Erreur lors de l'affichage des détails de l'offre : " + e.getMessage());
	        return "redirect:/login"; // Rediriger vers la page de connexion en cas d'erreur
	    }
	}




	//--------------------------------------------------------------------------------------------------------------------------------------------
	    @PostMapping("/supprimer-tontine")
	    public String deleteDaret(@RequestParam Long operationId) {
	        try {
	            // Récupérer la DaretOperation par ID
	            DaretOperation daretOperation = daretOperationService.findById(operationId);

	            // Vérifier si la DaretOperation est en cours
	            if ("En cours".equals(daretOperation.getStatus())) {
	                // Afficher une alerte SweetAlert pour l'annulation
	                return "redirect:/liste-des-tontines?deleteCanceled";
	            }

	            // Implémenter votre méthode de service pour supprimer la DaretOperation par ID
	            daretOperationService.deleteDaretById(operationId);

	            // Rediriger vers la vue de la liste après la suppression
	            return "redirect:/liste-des-tontines";
	        } catch (Exception e) {
	            // Gérer l'exception, vous pouvez la journaliser ou rediriger vers une page d'erreur
	            System.out.println("Erreur lors de la suppression de la DaretOperation : " + e.getMessage());
	            return "redirect:/liste-des-tontines?deleteError";
	        }
	    }


		//--------------------------------------------------------------------------------------------------------------------------------------------
	    @GetMapping("/liste-offres-pending")
	    public String listPendingOffers(Model model, @AuthenticationPrincipal UserDetails userDetails) {
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
	            // Gérer les exceptions, par exemple, rediriger vers une page d'erreur ou journaliser
	            System.out.println("Erreur lors de la récupération des offres en attente : " + ex.getMessage());
	            return "redirect:/login"; // Rediriger vers la page de connexion en cas d'erreur
	        }
	    }

		//--------------------------------------------------------------------------------------------------------------------------------------------
	   
	  


    
}
