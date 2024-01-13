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

import jakarta.servlet.http.HttpServletRequest;

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
	
	
	
	@GetMapping("/user-dashboard")
	public String userPage(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
	    User currentUser = userService.findByEmail(userDetails.getUsername());
	    String currentUrl = request.getRequestURL().toString();

	    long inProgressCount = daretOperationRepository.countByDaretParticipantsUserAndStatus(currentUser,"Progress");
	    long pendingCount = daretOperationRepository.countByDaretParticipantsUserAndStatus(currentUser,"Pending");
	    long closedCount = daretOperationRepository.countByDaretParticipantsUserAndStatus(currentUser,"Closed");

	    model.addAttribute("currentUrl", currentUrl)
	            .addAttribute("user", currentUser)
	            .addAttribute("pageTitle", "DARET-USER DASHBOARD")
	            .addAttribute("inProgressCount", inProgressCount)
	            .addAttribute("pendingCount", pendingCount)
	            .addAttribute("closedCount", closedCount);

	    return "User/UserDashboard";
	}



/*-----------------------------------------------------------------------------------------------------------------------------*/
																														
	@GetMapping("/admin-dashboard")
	public String adminPage (Model model,@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
	    User currentUser = userService.findByEmail(userDetails.getUsername());
		String currentUrl = request.getRequestURL().toString();
		long inProgressCount = daretOperationRepository.countByStatusAndAdminOffre("Progress", currentUser);
	    long pendingCount = daretOperationRepository.countByStatusAndAdminOffre("Pending", currentUser);
	    long closedCount = daretOperationRepository.countByStatusAndAdminOffre("Closed", currentUser);
	    long totalOffersCount = daretOperationRepository.countByAdminOffre(currentUser);
	    //---------------------------------------------------------------------------------------------------------
	    long inProgressCountSelf = daretOperationRepository.countByDaretParticipantsUserAndStatus(currentUser,"Progress");
	    long pendingCountSelf = daretOperationRepository.countByDaretParticipantsUserAndStatus(currentUser,"Pending");
	    long closedCountSelf = daretOperationRepository.countByDaretParticipantsUserAndStatus(currentUser,"Closed");
		model.addAttribute("currentUrl", currentUrl)
	        .addAttribute("user", currentUser)
	        .addAttribute("pageTitle", "DARET-ADMIN DASHBOARD ")
			.addAttribute("inProgressCount", inProgressCount)
		    .addAttribute("pendingCount", pendingCount)
		    .addAttribute("closedCount", closedCount)
		    .addAttribute("totalOffersCount", totalOffersCount)
		    .addAttribute("inProgressCountSelf", inProgressCountSelf)
            .addAttribute("pendingCountSelf", pendingCountSelf)
            .addAttribute("closedCountSelf", closedCountSelf);
		return "Admin/AdminDashboard";
	}
	
	/*------------------------------------------------------------------------------------------------*/
	 
}
