package com.mediatech.MoneyManagement.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.mediatech.MoneyManagement.DTO.UserDto;
import com.mediatech.MoneyManagement.Models.DaretOperation;
import com.mediatech.MoneyManagement.Models.User;
import com.mediatech.MoneyManagement.Repositories.DaretOperationRepository;
import com.mediatech.MoneyManagement.Repositories.UserRepository;
import com.mediatech.MoneyManagement.Services.DaretOperationService;
import com.mediatech.MoneyManagement.Services.UserService;


@Controller
public class UserController {
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private DaretOperationRepository daretOperationRepository;
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private DaretOperationService daretOperationService;

	@GetMapping("/registration")
	public String getRegistrationPage(@ModelAttribute("user") UserDto userDto) {
		return "Auth/register";
	}
	
	
	@PostMapping("/registration")
	public String saveUser(@ModelAttribute("user") UserDto userDto, Model model) {
	    // Check if passwords match
	    if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
	        model.addAttribute("error", "Passwords do not match.");
	        return "Auth/register";
	    }

	    // Check if the email already exists
	    if (userService.existsByEmail(userDto.getEmail())) {
	        model.addAttribute("error", "Email already exists. Please choose a different email.");
	        return "Auth/register"; 
	    }
	    // Check if the cin already exists
	    if (userService.existsByCin(userDto.getCin())) {
	        model.addAttribute("error", "CIN already exists. Please choose a different cin.");
	        return "Auth/register"; 
	    }
	    userService.save(userDto);
	    model.addAttribute("SuccessMessage", "Your account has been successfully created. You can now log in using your registered email and password.!");
	    return "redirect:/login?SuccessRegistration";
	}
	
	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("SuccessMessage", "You have successfully reset your password!");
		model.addAttribute("ErrorMessage", "Incorrect email or password. Please check your credentials and try again.!");
		return "Auth/login";
	}
	
	
/*--------------------------------------------------------------------------------------------------------------------*/	
	@GetMapping("/user-dashboard")
	public String userPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
	    try {
	        User currentUser = userService.findByEmail(userDetails.getUsername());
	        if (currentUser == null) {
	            // If currentUser is null, also redirect to the logout page
	            return "redirect:/logout";
	        }

	        long inProgressCount = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser, "Progress");
	        long pendingCount = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser, "Pending");
	        long closedCount = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser, "Closed");

	        // Add additional information to the model
	        model.addAttribute("user", currentUser)
	                .addAttribute("pageTitle", "DARET-USER DASHBOARD")
	                .addAttribute("inProgressCount", inProgressCount)
	                .addAttribute("pendingCount", pendingCount)
	                .addAttribute("closedCount", closedCount)
	                .addAttribute("additionalInfo", "Ceci est une information supplémentaire.");

	        return "layout/Dashboard";
	    } catch (Exception e) {
            return "redirect:/logout";
	    }
	}




/*-----------------------------------------------------------------------------------------------------------------------------*/
																														
	@GetMapping("/createur-dashboard")
	public String CreateurPage (Model model,@AuthenticationPrincipal UserDetails userDetails) {
		try {
			User currentUser = userService.findByEmail(userDetails.getUsername());
			if (currentUser == null) {
	            // If currentUser is null, also redirect to the logout page
	            return "redirect:/logout";
	        }
			long inProgressCount = daretOperationRepository.countByStatusAndAdminOffre("Progress", currentUser);
			long pendingCount = daretOperationRepository.countByStatusAndAdminOffre("Pending", currentUser);
			long closedCount = daretOperationRepository.countByStatusAndAdminOffre("Closed", currentUser);
			long totalOffersCount = daretOperationRepository.countByAdminOffre(currentUser);
			//---------------------------------------------------------------------------------------------------------
			long inProgressCountSelf = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser,"Progress");
			long pendingCountSelf = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser,"Pending");
			long closedCountSelf = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser,"Closed");
		
			model.addAttribute("user", currentUser)
			.addAttribute("pageTitle", "DARET-ADMIN DASHBOARD ")
			.addAttribute("inProgressCount", inProgressCount)
			.addAttribute("pendingCount", pendingCount)
			.addAttribute("closedCount", closedCount)
			.addAttribute("totalOffersCount", totalOffersCount)
			.addAttribute("inProgressCountSelf", inProgressCountSelf)
			.addAttribute("pendingCountSelf", pendingCountSelf)
			.addAttribute("closedCountSelf", closedCountSelf);			
			return "layout/Dashboard";
		}catch(Exception e) {
			return "redirect:/logout";
		}
	}
	
	/*------------------------------------------------------------------------------------------------*/
	@GetMapping("/admin-dashboard")
	public String adminPage (Model model,@AuthenticationPrincipal UserDetails userDetails) {
		try {
			User currentUser = userService.findByEmail(userDetails.getUsername());
			if (currentUser == null) {
	            // If currentUser is null, also redirect to the logout page
	            return "redirect:/logout";
	        }
			long inProgressCount = daretOperationRepository.countByStatusAndAdminOffre("Progress", currentUser);
			long pendingCount = daretOperationRepository.countByStatusAndAdminOffre("Pending", currentUser);
			long closedCount = daretOperationRepository.countByStatusAndAdminOffre("Closed", currentUser);
			//---------------------------------------------------------------------------------------------------------
			long inProgressCountSelf = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser,"Progress");
			long pendingCountSelf = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser,"Pending");
			long closedCountSelf = daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(currentUser,"Closed");
			//---------------------------------------------------------------------------------------------------------
			long AllTontineProgress = daretOperationRepository.countByStatus("Progress");
			long AllTontinePending = daretOperationRepository.countByStatus("Pending");
			long AllTontineClosed = daretOperationRepository.countByStatus("Closed");
			//---------------------------------------------------------------------------------------------------------
			long AllUsers = userRepository.countByRole("USER");
			long AllCreateur = userRepository.countByRole("CREATEUR");
			
			model.addAttribute("user", currentUser)
			.addAttribute("pageTitle", "DARET-ADMIN DASHBOARD ")
			.addAttribute("inProgressCount", inProgressCount)
			.addAttribute("pendingCount", pendingCount)
			.addAttribute("closedCount", closedCount)
			/*----------------------------------------------------*/
			.addAttribute("inProgressCountSelf", inProgressCountSelf)
			.addAttribute("pendingCountSelf", pendingCountSelf)
			.addAttribute("closedCountSelf", closedCountSelf)
			/*----------------------------------------------------*/
			.addAttribute("AllTontineProgress", AllTontineProgress)
			.addAttribute("AllTontinePending", AllTontinePending)
			.addAttribute("AllTontineClosed", AllTontineClosed)
			/*----------------------------------------------------*/
			.addAttribute("AllUsers", AllUsers)
			.addAttribute("AllCreateur", AllCreateur);
			return "layout/Dashboard";
		}catch(Exception e) {
			return "redirect:/logout";
		}
	}
	/*------------------------------------------------------------------------------------------------*/
	@GetMapping("/liste-utilisateurs")
	public String listeUsers (Model model,@AuthenticationPrincipal UserDetails userDetails) {
		try {
			User currentUser = userService.findByEmail(userDetails.getUsername());
			if (currentUser == null) {
	            // If currentUser is null, also redirect to the logout page
	            return "redirect:/logout";
	        }
	        List<User> allUsers = userService.findAllUsers();
			model.addAttribute("user", currentUser)
				 .addAttribute("allUsers", allUsers)
			     .addAttribute("pageTitle", "DARET-ADMIN LISTE-USERS");
			return "Admin/liste-utilisateurs";
		}catch(Exception e) {
			return "redirect:/logout";
		}
	}
	/*------------------------------------------------------------------------------------------------------*/
		@GetMapping("/liste-des-toutes-les-tontines")
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
		            userDaretOperations = daretOperationService.getAllDaretOperations();
		        } else {
		            userDaretOperations = daretOperationService.getAllDaretOperationsByStatus(status);
		        }

		        // Compter le nombre d'opérations dans différents états
		        long AllTontineProgress = daretOperationRepository.countByStatus("Progress");
				long AllTontinePending = daretOperationRepository.countByStatus("Pending");
				long AllTontineClosed = daretOperationRepository.countByStatus("Closed");
		        long totalOffersCount = daretOperationRepository.count();

		        // Ajouter les attributs au modèle pour affichage dans la vue
		        model.addAttribute("user", currentUser)
		            .addAttribute("userDaretOperations", userDaretOperations)
		            .addAttribute("inProgressCount", AllTontineProgress)
		            .addAttribute("pendingCount", AllTontinePending)
		            .addAttribute("closedCount", AllTontineClosed)
		            .addAttribute("totalOffersCount", totalOffersCount)
		            .addAttribute("selectedStatus", status);

		        // Renvoyer le nom de la vue pour afficher la liste des offres
		        return "Admin/liste-tontine";
		    } catch (Exception e) {
		        // Gérer les exceptions, par exemple, rediriger vers une page d'erreur ou journaliser
		        System.out.println("Erreur lors de la récupération des offres : " + e.getMessage());
		        return "redirect:/liste-des-tontines?error";
		    }
		}

}
