package com.mediatech.MoneyManagement.Controllers;

import java.time.LocalDate;

import java.util.List;

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
//-----------------------------------------------------------------------------------------------------------------------------------------
	    @PostMapping("/valider-payment/{daretOperationId}")
	    public String validerPayment(@PathVariable Long daretOperationId) {
	        try {
	            // Récupérer le DaretOperation depuis la base de données (assurez-vous que votre service ou repository est injecté)
	            DaretOperation daretOperation = daretOperationService.getDaretOperationById(daretOperationId);

	            // Mise à jour du verifyPayement et datePaiement pour tous les utilisateurs participant à ce DaretOperation
	            List<DaretParticipant> participants = daretOperation.getDaretParticipants();
	            for (DaretParticipant participant : participants) {
	                participant.setVerifyPayement(0);
	                if ("mensuelle".equalsIgnoreCase(daretOperation.getTypePeriode())) {
	                	participant.setDatePaiement(participant.getDatePaiement().plusMonths(1));	                	
	                }else if ("hebdomadaire".equalsIgnoreCase(daretOperation.getTypePeriode())) {
	                	participant.setDatePaiement(participant.getDatePaiement().plusDays(1));	                	
	                }else if ("semaine".equalsIgnoreCase(daretOperation.getTypePeriode())) {
	                	participant.setDatePaiement(participant.getDatePaiement().plusWeeks(1));	                	
	                }
	                
	            }
	            
	            // Enregistrez les modifications dans la base de données (assurez-vous que votre service ou repository est injecté)
	            daretOperationService.save(daretOperation);
	            // Vous pouvez ajouter d'autres logiques ici
	            // Rediriger vers une page de succès
	            return "redirect:/show-offer/" + daretOperation.getId();
	        } catch (Exception e) {
	            // Gérer les exceptions, pour l'instant, rediriger vers la page de connexion
	            return "redirect:/login";
	        }
	    }


    
}
