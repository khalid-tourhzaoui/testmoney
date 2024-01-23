package com.mediatech.MoneyManagement.Controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import com.mediatech.MoneyManagement.DTO.UserDto;
import com.mediatech.MoneyManagement.Models.User;
import com.mediatech.MoneyManagement.Repositories.DaretOperationRepository;
import com.mediatech.MoneyManagement.Services.UserService;


@Controller
public class UserController {
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private DaretOperationRepository daretOperationRepository;

	

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
	 
}
