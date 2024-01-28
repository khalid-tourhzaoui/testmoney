package com.mediatech.MoneyManagement.Controllers;

import java.time.LocalDate;


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
import com.mediatech.MoneyManagement.Repositories.DaretParticipantRepository;
import com.mediatech.MoneyManagement.Services.*;

@Controller
public class DaretParticipantController {
	@Autowired
	private DaretOperationService daretOperationService;
	@Autowired
	private DaretParticipantService daretParticipantService;
	@Autowired
	UserDetailsService userDetailsService;
	@Autowired
	private UserService userService;
	@Autowired
	private DaretOperationRepository daretOperationRepository;
	@Autowired
	private DaretParticipantRepository daretParticipantRepository;

//-------------------------------------------------------------------------------------------------------------------------------------------
	@GetMapping("/liste-des-participations")
	public String listeMesDarets(@RequestParam(name = "status", defaultValue = "All") String status,
	        Model model,RedirectAttributes redirectAttributes,
	        @AuthenticationPrincipal UserDetails userDetails) {
	    try {
	        // Vérifier si l'utilisateur actuel est null, ce qui indiquerait une session terminée
	        if (userDetails == null) {
	        	redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
	            // Rediriger vers la page de déconnexion
	            return "redirect:/logout";
	        }

	        // Récupérer l'utilisateur actuel
	        User currentUser = userService.findByEmail(userDetails.getUsername());
	        List<DaretOperation> userDaretOperations;

	        // Vérifier le statut et récupérer les opérations correspondantes
	       if (status.equals("All")) {
	            userDaretOperations = daretOperationService.findByDaretParticipantsUser(currentUser);
	        }else {
	            userDaretOperations = daretOperationService.findByDaretParticipantsUserAndStatus(currentUser, status);
	        }

	        // Compter le nombre d'opérations dans différents états
	        long inProgressCount = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser, "Progress");
	        long pendingCount = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser, "Pending");
	        long closedCount = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser, "Closed");
	        long totalDaretsCount = daretOperationRepository.countDistinctByDaretParticipantsUser(currentUser);

	        // Ajouter les attributs au modèle pour affichage dans la vue
	        model.addAttribute("user", currentUser)
	                .addAttribute("userDaretOperations", userDaretOperations)
	                .addAttribute("inProgressCount", inProgressCount)
	                .addAttribute("pendingCount", pendingCount)
	                .addAttribute("closedCount", closedCount)
	                .addAttribute("totalDaretsCount", totalDaretsCount)
	                .addAttribute("selectedStatus", status)
	                .addAttribute("pageTitle", "DARET-ADMIN LISTE MES DARETS");

	        return "Admin/liste-tontine";
	    } catch (Exception e) {
	    	redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
	        return "redirect:/logout";
	    }
	}

//--------------------------------------------------------------------------------------------------------------------------------------------
	@PostMapping("/liste-offres-pending/ajouter-participant")
	public String addParticipantToDaretOperation(
			@RequestParam("daretOperationId") Long daretOperationId,
			@RequestParam("userId") Long userId,
			@RequestParam("paymentType") String paymentType,
			@RequestParam("montantPaye") float montantPaye,
			Model model,RedirectAttributes redirectAttributes) {
		try {
			// Récupérer l'opération Daret correspondante
			DaretOperation daretOperation = daretOperationService.findById(daretOperationId);

			// Vérifier si le nombre de places disponibles est suffisant
			if (daretOperation.getPlacesReservees() >= daretOperation.getNombreParticipant()) {
				redirectAttributes.addFlashAttribute("errorMessage", "Le nombre de places disponibles est insuffisant.");
				return "redirect:/liste-offres-pending";
			}

			// Valider le type de paiement (vous pouvez ajouter davantage de validations si
			// nécessaire)
			if (!paymentType.equals("Moitier") && !paymentType.equals("Normale") && !paymentType.equals("Double")) {
				// Gérer le type de paiement invalide, par exemple, rediriger vers une page
				// d'erreur
				redirectAttributes.addFlashAttribute("errorMessage", "Le type de paiement doit être normale, double ou moitier");
				return "redirect:/liste-offres-pending";
			}

			// Appeler la méthode de service pour ajouter le participant et mettre à jour
			// les placesReservees
			daretParticipantService.addParticipantToDaretOperation(daretOperationId, userId, paymentType, montantPaye);
        	redirectAttributes.addFlashAttribute("successMessage", "L'offre a été ajoutée avec succès");
			return "redirect:/liste-offres-pending";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
			return "redirect:/logout";
		}
	}

//-----------------------------------------------------------------------------------------------------------------------------------------
	@PostMapping("/passer-paiement/{participantId}")
	public String makePayment(@PathVariable Long participantId, Model model,RedirectAttributes redirectAttributes) {
	    try {
	        DaretParticipant participant = daretParticipantService.getDaretParticipantById(participantId);

	        // Check if datePaiement is null
	        if (participant.getDatePaiement() == null) {
	            // Get the previous participant's payment date
	            DaretParticipant previousParticipant = getPreviousParticipant(participant);
	            if (previousParticipant != null) {
	                participant.setDatePaiement(previousParticipant.getDatePaiement());
	            } else {
	            	redirectAttributes.addFlashAttribute("errorMessage", "La date de paiement n'est pas définie et il n'y a pas de participant précédent.");
	                return "redirect:/details-tontine/" + participant.getDaretOperation().getId();
	            }
	        }

	        // Check conditions before updating verifyPayement
	        LocalDate currentDate = LocalDate.now();
	        LocalDate paymentDate = participant.getDatePaiement();

	        if (currentDate.isEqual(paymentDate) && participant.getVerifyPayement() == 0) {
	            // Conditions met, update verifyPayement
	            participant.setVerifyPayement(1);
	            // Save the updated participant
	            daretParticipantRepository.save(participant);
	        } else {
	        	redirectAttributes.addFlashAttribute("errorMessage", "La date de paiement n'est pas valide.");
	            return "redirect:/details-tontine/" + participant.getDaretOperation().getId();
	        }

	        // Redirect to the appropriate page
        	redirectAttributes.addFlashAttribute("successMessage", "Le paiement a été effectuée avec succès");
	        return "redirect:/details-tontine/" + participant.getDaretOperation().getId();
	    } catch (Exception e) {
	    	redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
	        return "redirect:/logout";
	    }
	}

	private DaretParticipant getPreviousParticipant(DaretParticipant currentParticipant) {
	    // Get the list of participants for the current DaretOperation
	    List<DaretParticipant> participants = currentParticipant.getDaretOperation().getDaretParticipants();

	    // Find the index of the current participant
	    int currentIndex = participants.indexOf(currentParticipant);

	    // If the current participant is not the first one, return the previous participant
	    if (currentIndex > 0) {
	        return participants.get(currentIndex - 1);
	    }

	    return null; // Return null if there is no previous participant
	}

	/*------------------------------------------------------------------------------------------*/
	@PostMapping("/valider-paiement/{daretOperationId}")
	public String validerPayment(@PathVariable Long daretOperationId,RedirectAttributes redirectAttributes) {
		try {
			// Récupérer le DaretOperation depuis la base de données
			DaretOperation daretOperation = daretOperationService.getDaretOperationById(daretOperationId);

			// Mise à jour du verifyPayement et datePaiement pour tous les utilisateurs
			// participant à ce DaretOperation
			List<DaretParticipant> participants = daretOperation.getDaretParticipants();
			boolean allPaymentsReceived = true;

			for (DaretParticipant participant : participants) {
				if (participant.getVerifyPayement() == 0) {
					allPaymentsReceived = false;
					break; // Sortir de la boucle si un paiement est manquant
				}
			}

			if (allPaymentsReceived) {
				// Tous les paiements ont été reçus, mise à jour du tour de rôle
				updateTourDeRole(daretOperation);
				// Réinitialiser verifyPayement et ajuster datePaiement pour le prochain cycle
				int currentTourDeRole = daretOperation.getTourDeRole();
				for (DaretParticipant participant : participants) {
					participant.setVerifyPayement(0);
					calculateNextPaymentDate(participant, daretOperation);
					if ("Double".equalsIgnoreCase(participant.getTypePayement())
							&& "current".equalsIgnoreCase(participant.getEtatTour())) {
						handleDoubleParticipantTour(participant, currentTourDeRole, participants);
					} else {
						handleRegularParticipantTour(participant, currentTourDeRole);
					}

				}

				// Enregistrez les modifications dans la base de données
				daretOperationService.save(daretOperation);

				// Vérifier si c'est le dernier tour de rôle après avoir mis à jour les
				// participants
				if (LocalDate.now().isEqual(daretOperation.getDateFin())) {
					// Changer le statut de la DaretOperation en "closed"
					daretOperation.setStatus("closed");
					// Enregistrez la mise à jour du statut dans la base de données
					daretOperationService.save(daretOperation);
	            	redirectAttributes.addFlashAttribute("successMessage", "La tontine a été terminée avec succès");
					return "redirect:/liste-des-tontines";
				}
				// Rediriger vers une page de succès
				return "redirect:/details-tontine/" + daretOperation.getId();
			} else {
				// Certains paiements sont manquants, rediriger vers une page d'erreur ou une
				// autre page
            	redirectAttributes.addFlashAttribute("errorMessage", "Certains paiements sont manquants");
				return "redirect:/details-tontine/" + daretOperation.getId();
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite. Veuillez réessayer.");
			return "redirect:/logout";
		}
	}

	private void handleDoubleParticipantTour(DaretParticipant participant, int currentTourDeRole,
			List<DaretParticipant> participants) {
		// Logique pour gérer le tour de rôle pour le participant de type "Double"
		if ("current".equalsIgnoreCase(participant.getEtatTour())) {
			int participantTourIndex = participant.getCoupleIndex() + 1;
			System.out.println(participantTourIndex);

			// Vérifier si le participant de type "Double" a déjà effectué deux tours
			if (participantTourIndex == currentTourDeRole) {
				participant.setEtatTour("current");
				for (DaretParticipant otherParticipant : participants) {
					if (!participant.equals(otherParticipant)
							&& "not_done".equalsIgnoreCase(otherParticipant.getEtatTour())) {
						otherParticipant.setCoupleIndex(otherParticipant.getCoupleIndex() + 1);
					}
				}
			} else if (participantTourIndex == currentTourDeRole + 1) {
				// Utilisez "secondtour" pour représenter le deuxième tour
				participant.setEtatTour("current");
			} else if (participantTourIndex < currentTourDeRole) {
				participant.setEtatTour("done");
			} else {
				participant.setEtatTour("not_done");

			}
		}
	}

	private void handleRegularParticipantTour(DaretParticipant participant, int currentTourDeRole) {
		// Logique pour gérer le tour de rôle pour les participants normaux
		if (participant.getCoupleIndex() == currentTourDeRole) {
			participant.setEtatTour("current");
		} else if (participant.getCoupleIndex() < currentTourDeRole) {
			participant.setEtatTour("done");
		} else {
			participant.setEtatTour("not_done");
		}
	}

	private void updateTourDeRole(DaretOperation daretOperation) {
		int currentTourDeRole = daretOperation.getTourDeRole();
		daretOperation.setTourDeRole(currentTourDeRole + 1);
	}

	// Calculez la prochaine date de paiement en fonction de la période
	public void calculateNextPaymentDate(DaretParticipant participant, DaretOperation daretOperation) {
		if ("Mensuelle".equalsIgnoreCase(daretOperation.getTypePeriode())) {
			participant.setDatePaiement(participant.getDatePaiement().plusMonths(1));
		} else if ("Quotidienne".equalsIgnoreCase(daretOperation.getTypePeriode())) {
			participant.setDatePaiement(participant.getDatePaiement().plusDays(1));
		} else if ("Hebdomadaire".equalsIgnoreCase(daretOperation.getTypePeriode())) {
			participant.setDatePaiement(participant.getDatePaiement().plusWeeks(1));
		}
	}

}
