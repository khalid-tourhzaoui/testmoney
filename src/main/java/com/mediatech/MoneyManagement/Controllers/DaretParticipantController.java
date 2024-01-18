package com.mediatech.MoneyManagement.Controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mediatech.MoneyManagement.Models.DaretOperation;
import com.mediatech.MoneyManagement.Models.DaretParticipant;
import com.mediatech.MoneyManagement.Repositories.DaretParticipantRepository;
import com.mediatech.MoneyManagement.Services.DaretOperationService;
import com.mediatech.MoneyManagement.Services.DaretParticipantService;



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
	    @PostMapping("/liste-offres-pending/add-participant")
	    public String addParticipantToDaretOperation(
	            @RequestParam("daretOperationId") Long daretOperationId,
	            @RequestParam("userId") Long userId,
	            @RequestParam("paymentType") String paymentType,
	            @RequestParam("montantPaye") float montantPaye,  // Add this parameter
	            Model model
	    ) {
	        // Validate payment type (you might want to add more validation)
	        if (!paymentType.equals("Moitier") && !paymentType.equals("Normale") && !paymentType.equals("Double")) {
	            // Handle invalid payment type, e.g., redirect to an error page
	            return "redirect:/error";
	        }
	        // Call the service method to add participant and update placesReservees
	        daretParticipantService.addParticipantToDaretOperation(daretOperationId, userId, paymentType, montantPaye);

	        return "redirect:/liste-offres-pending";
	    }
//-----------------------------------------------------------------------------------------------------------------------------------------
	    @PostMapping("/make-payment/{participantId}")
	    public String makePayment(@PathVariable Long participantId) {
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
	            }

	            // Redirect to the appropriate page
	            return "redirect:/show-offer/" + participant.getDaretOperation().getId();
	        } catch (Exception e) {
	            // Handle exceptions if needed
	            return "redirect:/error";
	        }
	    }

/*------------------------------------------------------------------------------------------*/
	    @PostMapping("/valider-payment/{daretOperationId}")
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

	                    if ("Double".equalsIgnoreCase(participant.getTypePayement()) && "current".equalsIgnoreCase(participant.getEtatTour())) {
	                        handleDoubleParticipantTour(participant, currentTourDeRole,participants);
	                    } else {
	                        handleRegularParticipantTour(participant, currentTourDeRole);
	                    }
	                }

	                // Enregistrez les modifications dans la base de données
	                daretOperationService.save(daretOperation);

	                // Vous pouvez ajouter d'autres logiques ici
	                // Rediriger vers une page de succès
	                return "redirect:/show-offer/" + daretOperation.getId();
	            } else {
	                // Certains paiements sont manquants, rediriger vers une page d'erreur ou une autre page
	                return "redirect:/payment-error";
	            }
	        } catch (Exception e) {
	            // Gérer les exceptions, pour l'instant, rediriger vers la page de connexion
	            return "redirect:/login";
	        }
	    }


	   /* private void handleDoubleParticipantTour(DaretParticipant participant, int currentTourDeRole) {
	        // Logique pour gérer le tour de rôle pour le participant de type "Double"
	        if ("current".equalsIgnoreCase(participant.getEtatTour())) {
	            int participantTourIndex = participant.getCoupleIndex()+1;
	            System.out.println(participantTourIndex);
	            // Vérifier si le participant de type "Double" a déjà effectué deux tours
	            if (participantTourIndex == currentTourDeRole) {
	                participant.setEtatTour("current");
	            } else if (participantTourIndex == currentTourDeRole + 1) {
	                participant.setEtatTour("current");
	            } else if (participantTourIndex < currentTourDeRole) {
	                participant.setEtatTour("done");
	            } else {
	                participant.setEtatTour("not_done");
	            }
	        }
	    }*/

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
