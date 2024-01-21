package com.mediatech.MoneyManagement.Controllers;

import java.time.LocalDate;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.mediatech.MoneyManagement.Models.*;
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
    private DaretParticipantRepository daretParticipantRepository;
	
		
//--------------------------------------------------------------------------------------------------------------------------------------------
	@PostMapping("/liste-offres-pending/ajouter-participant")
	public String addParticipantToDaretOperation(
	        @RequestParam("daretOperationId") Long daretOperationId,
	        @RequestParam("userId") Long userId,
	        @RequestParam("paymentType") String paymentType,
	        @RequestParam("montantPaye") float montantPaye,
	        Model model
	) {
	    try {
	        // Récupérer l'opération Daret correspondante
	        DaretOperation daretOperation = daretOperationService.findById(daretOperationId);

	        // Vérifier si le nombre de places disponibles est suffisant
	        if (daretOperation.getPlacesReservees() >= daretOperation.getNombreParticipant()) {
	            // Le nombre de places disponibles est insuffisant, gérer cela en affichant un message d'erreur ou en redirigeant
	            model.addAttribute("errorMessage", "Le nombre de places disponibles est insuffisant.");
	            return "redirect:/liste-offres-pending";
	        }

	        // Valider le type de paiement (vous pouvez ajouter davantage de validations si nécessaire)
	        if (!paymentType.equals("Moitier") && !paymentType.equals("Normale") && !paymentType.equals("Double")) {
	            // Gérer le type de paiement invalide, par exemple, rediriger vers une page d'erreur
	            model.addAttribute("errorMessage", "Le type de paiement doit être normale, double ou moitier");
	            return "redirect:/liste-offres-pending";
	        }

	        // Appeler la méthode de service pour ajouter le participant et mettre à jour les placesReservees
	        daretParticipantService.addParticipantToDaretOperation(daretOperationId, userId, paymentType, montantPaye);

	        return "redirect:/liste-offres-pending";
	    } catch (Exception e) {
	        // Gérer l'exception, par exemple, rediriger vers une page d'erreur ou journaliser
	        model.addAttribute("errorMessage", "Une erreur s'est produite lors de l'ajout du participant.");
	        return "redirect:/liste-offres-pending";
	    }
	}

//-----------------------------------------------------------------------------------------------------------------------------------------
	    @PostMapping("/passer-paiement/{participantId}")
	    public String makePayment(@PathVariable Long participantId,Model model) {
	        try {
	            DaretParticipant participant = daretParticipantService.getDaretParticipantById(participantId);

	            // Check conditions before updating verifyPayement
	            LocalDate currentDate = LocalDate.now();
	            LocalDate paymentDate = participant.getDatePaiement();
	            
	            if (currentDate.isEqual(paymentDate) && participant.getVerifyPayement() == 0) {
	                // Conditions met, update verifyPayement
	                participant.setVerifyPayement(1);
	                // Save the updated participant
	                daretParticipantRepository.save(participant);
	            }else {
	            	model.addAttribute("ErrorMessage", "La date de paiement n'est pas valide.");
	            	return "redirect:/details-offre/" + participant.getDaretOperation().getId();
	            }

	            // Redirect to the appropriate page
	            return "redirect:/details-offre/" + participant.getDaretOperation().getId();
	        } catch (Exception e) {
	            // Handle exceptions if needed
	            return "redirect:/logout";
	        }
	    }

/*------------------------------------------------------------------------------------------*/
	    @PostMapping("/valider-paiement/{daretOperationId}")
	    public String validerPayment(@PathVariable Long daretOperationId) {
	        try {
	            // Récupérer le DaretOperation depuis la base de données
	            DaretOperation daretOperation = daretOperationService.getDaretOperationById(daretOperationId);

	            // Mise à jour du verifyPayement et datePaiement pour tous les utilisateurs participant à ce DaretOperation
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
	                    if ("Double".equalsIgnoreCase(participant.getTypePayement()) && "current".equalsIgnoreCase(participant.getEtatTour())) {
	                        handleDoubleParticipantTour(participant, currentTourDeRole,participants);
	                    } else {
	                        handleRegularParticipantTour(participant, currentTourDeRole);
	                    }
	                   
	                }

	                // Enregistrez les modifications dans la base de données
	                daretOperationService.save(daretOperation);

	                // Vérifier si c'est le dernier tour de rôle après avoir mis à jour les participants
	                if (LocalDate.now().isEqual(daretOperation.getDateFin())) {
	                    // Changer le statut de la DaretOperation en "closed"
	                    daretOperation.setStatus("closed");
	                    // Enregistrez la mise à jour du statut dans la base de données
	                    daretOperationService.save(daretOperation);
	                    return "redirect:/admin-dashboard";
	                }
	                // Rediriger vers une page de succès
	                return "redirect:/details-offre/" + daretOperation.getId();
	            } else {
	                // Certains paiements sont manquants, rediriger vers une page d'erreur ou une autre page
	                return "redirect:/payment-error";
	            }
	        } catch (Exception e) {
	            // Gérer les exceptions, pour l'instant, rediriger vers la page de connexion
	            return "redirect:/login";
	        }
	    }


	    private void handleDoubleParticipantTour(DaretParticipant participant, int currentTourDeRole, List<DaretParticipant> participants) {
	        // Logique pour gérer le tour de rôle pour le participant de type "Double"
	        if ("current".equalsIgnoreCase(participant.getEtatTour())) {
	            int participantTourIndex = participant.getCoupleIndex() + 1;
	            System.out.println(participantTourIndex);

	            // Vérifier si le participant de type "Double" a déjà effectué deux tours
	            if (participantTourIndex == currentTourDeRole) {
	                participant.setEtatTour("current");
	                for (DaretParticipant otherParticipant : participants) {
	                	if (!participant.equals(otherParticipant) && "not_done".equalsIgnoreCase(otherParticipant.getEtatTour())) {
	                		otherParticipant.setCoupleIndex(otherParticipant.getCoupleIndex()+1);	                		
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
	        if ("mensuelle".equalsIgnoreCase(daretOperation.getTypePeriode())) {
	            participant.setDatePaiement(participant.getDatePaiement().plusMonths(1));
	        } else if ("hebdomadaire".equalsIgnoreCase(daretOperation.getTypePeriode())) {
	            participant.setDatePaiement(participant.getDatePaiement().plusDays(1));
	        } else if ("semaine".equalsIgnoreCase(daretOperation.getTypePeriode())) {
	            participant.setDatePaiement(participant.getDatePaiement().plusWeeks(1));
	        }
	    }


}
