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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
	public String getRegistrationPage(@ModelAttribute("user") UserDto userDto,RedirectAttributes redirectAttributes) {
	    try {
	        // Votre code existant pour gérer la requête GET et préparer le modèle
	        return "Auth/register"; // Le nom de la vue/template
	    } catch (Exception e) {
	    	redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors du traitement de votre demande.");
	        return "redirect:/login";
	    }
	}


	
	
	@PostMapping("/registration")
	public String saveUser(@ModelAttribute("user") UserDto userDto, RedirectAttributes redirectAttributes) {
	    try {
	        // Vérifier si les mots de passe correspondent
	        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
	            redirectAttributes.addFlashAttribute("errorMessage", "Les mots de passe ne correspondent pas.");
	            return "Auth/register";
	        }

	        // Vérifier si l'e-mail existe déjà
	        if (userService.existsByEmail(userDto.getEmail())) {
	            redirectAttributes.addFlashAttribute("errorMessage", "L'e-mail existe déjà. Veuillez choisir un e-mail différent.");
	            return "Auth/register";
	        }

	        // Vérifier si le CIN existe déjà
	        if (userService.existsByCin(userDto.getCin())) {
	            redirectAttributes.addFlashAttribute("errorMessage", "CIN existe déjà. Veuillez choisir un CIN différent.");
	            return "Auth/register";
	        }

	        // Enregistrer l'utilisateur
	        userService.save(userDto);

	        // Ajouter un message de succès pour la redirection
	        redirectAttributes.addFlashAttribute("successMessage", "Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter.");

	        // Rediriger vers la page de connexion avec un paramètre de succès
	        return "redirect:/login";
	    } catch (Exception e) {
	        // Gérer l'exception en cas d'erreur lors de l'enregistrement
	        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors de la création du compte.");
	        return "Auth/register";
	    }
	}

	
	@GetMapping("/login")
	public String login(RedirectAttributes redirectAttributes) {
	    try {
	        return "Auth/login";
	    } catch (Exception e) {
	        // Ajouter un attribut flash pour le message d'erreur
	    	redirectAttributes.addFlashAttribute("errorMessage", "Échec de la connexion. Veuillez vérifier vos informations d'identification.");
	        // Rediriger vers la page de connexion avec l'attribut flash d'erreur
	        return "redirect:/login";
	    }
	}

	
	
/*--------------------------------------------------------------------------------------------------------------------*/	
	@GetMapping("/user-dashboard")
	public String userPage(Model model, @AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes) {
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
	        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors du chargement du tableau de bord de l'utilisateur.");
            return "redirect:/logout";
	    }
	}

/*-----------------------------------------------------------------------------------------------------------------------------*/
																														
	@GetMapping("/createur-dashboard")
	public String CreateurPage (Model model,@AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes) {
		try {
			User currentUser = userService.findByEmail(userDetails.getUsername());
			if (currentUser == null) {
		        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors du chargement du tableau de bord de l'utilisateur.");
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
	        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors du chargement du tableau de bord de l'utilisateur.");
			return "redirect:/logout";
		}
	}
	
	/*------------------------------------------------------------------------------------------------*/
	@GetMapping("/admin-dashboard")
	public String adminPage (Model model,@AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes) {
		try {
			User currentUser = userService.findByEmail(userDetails.getUsername());
			if (currentUser == null) {
	            // If currentUser is null, also redirect to the logout page
		        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors du chargement du tableau de bord de l'utilisateur.");
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
	        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors du chargement du tableau de bord de l'utilisateur.");
			return "redirect:/logout";
		}
	}
	/*------------------------------------------------------------------------------------------------*/
	@GetMapping("/liste-utilisateurs")
	public String listeUsers (Model model,@AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes) {
		try {
			User currentUser = userService.findByEmail(userDetails.getUsername());
			if (currentUser == null) {
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
	public String listeOffres(@RequestParam(name = "status", defaultValue = "All") String status,
	                           Model model,
	                           @AuthenticationPrincipal UserDetails userDetails,
	                           RedirectAttributes redirectAttributes) {
	    try {
	        // Vérifier si l'utilisateur actuel est null, ce qui indiquerait une session terminée
	        if (userDetails == null) {
	            // Rediriger vers la page de déconnexion avec un message d'erreur
	            redirectAttributes.addFlashAttribute("errorMessage", "Votre session a expiré. Veuillez vous reconnecter.");
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
	        long allTontineProgress = daretOperationRepository.countByStatus("Progress");
	        long allTontinePending = daretOperationRepository.countByStatus("Pending");
	        long allTontineClosed = daretOperationRepository.countByStatus("Closed");
	        long totalOffersCount = daretOperationRepository.count();

	        // Ajouter les attributs au modèle pour affichage dans la vue
	        model.addAttribute("user", currentUser)
	             .addAttribute("userDaretOperations", userDaretOperations)
	             .addAttribute("inProgressCount", allTontineProgress)
	             .addAttribute("pendingCount", allTontinePending)
	             .addAttribute("closedCount", allTontineClosed)
	             .addAttribute("totalOffersCount", totalOffersCount)
	             .addAttribute("selectedStatus", status);

	        // Renvoyer le nom de la vue pour afficher la liste des offres
	        return "Admin/liste-tontine";
	    } catch (Exception e) {
	        // Rediriger vers la page de liste des tontines avec un message d'erreur
	        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors de la récupération des tontines.");
	        return "redirect:/liste-des-tontines";
	    }
	}

}
