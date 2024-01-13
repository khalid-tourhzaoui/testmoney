package com.mediatech.MoneyManagement.Controllers;



import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mediatech.MoneyManagement.Models.DaretOperation;
import com.mediatech.MoneyManagement.Models.DaretParticipant;
import com.mediatech.MoneyManagement.Models.User;
import com.mediatech.MoneyManagement.Repositories.DaretOperationRepository;
import com.mediatech.MoneyManagement.Services.DaretOperationService;
import com.mediatech.MoneyManagement.Services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


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
	

	

	@GetMapping("/liste-des-offres")
	public String listeOffres(
	    @RequestParam(name = "status", defaultValue = "All") String status,
	    Model model,
	    @AuthenticationPrincipal UserDetails userDetails,
	    HttpServletRequest request
	) {
	    User currentUser = userService.findByEmail(userDetails.getUsername());
	    List<DaretOperation> userDaretOperations;

	    if (status.equals("All")) {
	        userDaretOperations = daretOperationService.findByAdminOffre(currentUser);
	    } else {
	        userDaretOperations = daretOperationService.findByAdminOffreAndStatus(currentUser, status);
	    }

	    long inProgressCount = daretOperationRepository.countByStatusAndAdminOffre("Progress", currentUser);
	    long pendingCount = daretOperationRepository.countByStatusAndAdminOffre("Pending", currentUser);
	    long closedCount = daretOperationRepository.countByStatusAndAdminOffre("Closed", currentUser);
	    long totalOffersCount = daretOperationRepository.countByAdminOffre(currentUser);

	    model.addAttribute("currentUrl", request.getRequestURL().toString())
	    	.addAttribute("user", currentUser)
		    .addAttribute("userDaretOperations", userDaretOperations)
		    .addAttribute("inProgressCount", inProgressCount)
		    .addAttribute("pendingCount", pendingCount)
		    .addAttribute("closedCount", closedCount)
		    .addAttribute("totalOffersCount", totalOffersCount)
		    .addAttribute("selectedStatus", status)
        	.addAttribute("pageTitle", "DARET-ADMIN OFFER LIST");

	    return "Admin/liste-offres";
	}

	//-------------------------------------------------------------------------------------------------------------------------------------------
	    @GetMapping("/ajouter-offre")
		public String AddOffre(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
	    	// Get the currently authenticated user details
		    User currentUser = userService.findByEmail(userDetails.getUsername());
		    String currentUrl = request.getRequestURL().toString();
		    model.addAttribute("currentUrl", currentUrl);
		    // Add the authenticated user details to the model
		    model.addAttribute("user", currentUser);
	        DaretOperation daretOperation = new DaretOperation();
	        model.addAttribute("daretOperation", daretOperation)
        		.addAttribute("pageTitle", "DARET-ADMIN ADD OFFER");

	        // Return the view name for the add offer form
	        return "Admin/ajouter-offre";
	    }
	//------------------------------------------------------------------------------------------------------------------------------------------
	    @PostMapping("/ajouter-offre")
	    public String saveOffer(@ModelAttribute("daretOperation") @Valid DaretOperation daretOperation,
	                            BindingResult bindingResult,
	                            @AuthenticationPrincipal UserDetails userDetails,
	                            Model model) {

	        if (bindingResult.hasErrors()) {
	            // If there are validation errors, return to the form page with error messages
	            return "Admin/ajouter-offre";
	        }

	        User currentUser = userService.findByEmail(userDetails.getUsername());
	        daretOperation.setAdminOffre(currentUser);
	        daretOperation.setStatus("Pending");
	        daretOperation.setDateDebut(null);
	        daretOperation.setDateFin(null);
	        daretOperation.setTourDeRole(1L);

	        daretOperationService.save(daretOperation);

	        return "redirect:/liste-des-offres";
	    }
	/*-------------------------------------------------------------------------------------------------------------------------------------------*/
	    @GetMapping("/edit-offer/{operationId}")                                                                                                 
	    public String showUpdateForm(@PathVariable Long operationId, Model model,
	                                 @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
	        try {
	            User currentUser = userService.findByEmail(userDetails.getUsername());
	            String currentUrl = request.getRequestURL().toString();
	            model.addAttribute("currentUrl", currentUrl);
	            model.addAttribute("user", currentUser);

	            // Retrieve the DaretOperation by ID
	            DaretOperation daretOperation = daretOperationService.findById(operationId);

	            // Perform authorization check here if needed

	            // If the DaretOperation is not in progress, show the update form
	            model.addAttribute("daretOperation", daretOperation)
	                 .addAttribute("pageTitle", "DARET-ADMIN UPDATE OFFER");

	            // Return the view name for the update offer form
	            return "Admin/edit-offer";
	        } catch (Exception xe) {
	            // Consider redirecting to a more user-friendly error page or login page
	            return "redirect:/login";
	        }
	    }


	    //-------------------------------------------------------------------------------------------------------------------------------------------
	    @PostMapping("/edit-offer/{operationId}")
	    public String updateOffer(@PathVariable Long operationId, 
	                              @ModelAttribute("daretOperation") DaretOperation updatedDaretOperation,
	                              @AuthenticationPrincipal UserDetails userDetails) {
	        User currentUser = userService.findByEmail(userDetails.getUsername());
	        // Assuming you have a method to retrieve the existing DaretOperation
	        DaretOperation existingDaretOperation = daretOperationService.findById(operationId);

	        if (existingDaretOperation != null) {
	            
	            existingDaretOperation.setDesignation(updatedDaretOperation.getDesignation());
	            existingDaretOperation.setNombreParticipant(updatedDaretOperation.getNombreParticipant());
	            existingDaretOperation.setMontantParPeriode(updatedDaretOperation.getMontantParPeriode());
	            existingDaretOperation.setTypePeriode(updatedDaretOperation.getTypePeriode());
	            updatedDaretOperation.setAdminOffre(currentUser);
		        updatedDaretOperation.setStatus("Pending");
		        updatedDaretOperation.setTourDeRole(1L);

	            
	            // Save the updatedDaretOperation
	            daretOperationService.save(existingDaretOperation);
	        }

	        return "redirect:/liste-des-offres?updateSuccess";
	    }



	//--------------------------------------------------------------------------------------------------------------------------------------------
	    @GetMapping("/show-offer/{operationId}")
	    public String showOfferDetails(@PathVariable Long operationId, @AuthenticationPrincipal UserDetails userDetails,
	            Model model, HttpServletRequest request) {
	        try {
	            User currentUser = userService.findByEmail(userDetails.getUsername());
	            String currentUrl = request.getRequestURL().toString();
	            model.addAttribute("currentUrl", currentUrl);
	            model.addAttribute("user", currentUser);

	            // Retrieve the DaretOperation by ID
	            DaretOperation daretOperation = daretOperationService.findById(operationId);
	            List<DaretParticipant> participants = daretOperation.getDaretParticipants();

	            // Perform authorization check here if needed

	            // If the DaretOperation is not in progress, show the update form
	            model.addAttribute("daretOperation", daretOperation)
	                 .addAttribute("participants", participants)
	                 .addAttribute("pageTitle", "DARET-ADMIN UPDATE OFFER");

	            // Return the view name for the update offer form
	            return "Admin/show-offer";

	        } catch (Exception xe) {
	            // Consider redirecting to a more user-friendly error page or login page
	            return "redirect:/login";
	        }
	    }



	//--------------------------------------------------------------------------------------------------------------------------------------------

	    @PostMapping("/delete-daret")
	    public String deleteDaret(@RequestParam Long operationId) {
	        // Retrieve the DaretOperation by ID
	        DaretOperation daretOperation = daretOperationService.findById(operationId);

	        // Check if the DaretOperation is in progress
	        if ("In Progress".equals(daretOperation.getStatus())) {
	            // Display a SweetAlert for cancellation
	            return "redirect:/liste-des-offres?deleteCanceled";
	        }

	        // Implement your service method to delete the DaretOperation by ID
	        daretOperationService.deleteDaretById(operationId);

	        // Redirect to the list view after deletion
	        return "redirect:/liste-des-offres?deleteSuccess";
	    }

		//--------------------------------------------------------------------------------------------------------------------------------------------
	    @GetMapping("/liste-offres-pending")
	    public String listPendingOffers(Model model, @AuthenticationPrincipal UserDetails userDetails) {
	    	try {
		        // Get the currently authenticated user details
		        User currentUser = userService.findByEmail(userDetails.getUsername());
	
		        // Get a list of offers with status "Pending" for all admins
		        List<DaretOperation> pendingOffers = daretOperationService.findPendingOffers();
	
		        // Add the authenticated user details and the list of pending offers to the model
		        model.addAttribute("user", currentUser)
		        	.addAttribute("pendingOffers", pendingOffers)
		        	.addAttribute("pageTitle", "DARET-ADMIN UPDATE OFFER");
		        System.out.println(pendingOffers);
		        // Return the view name for displaying the list of pending offers
		        return "Admin/liste-offres-pending";
	    	}catch(Exception ex) {
	    		return "redirect:/login";
	    	}
	    }
		//--------------------------------------------------------------------------------------------------------------------------------------------
	   
	  


    
}
