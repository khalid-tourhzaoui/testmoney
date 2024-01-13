package com.mediatech.MoneyManagement.Controllers;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.mediatech.MoneyManagement.Models.User;
import com.mediatech.MoneyManagement.Services.UserService;


@Controller
public class ProfileController {

    @Autowired
    private UserService userService;
    /*---------------------------------------------------------------------------------------------------------------*/
    @GetMapping("profile")
	public String userProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
	    // userDetails contains information about the authenticated user
	    User currentUser = userService.findByEmail(userDetails.getUsername());
	    // Add the user object to the model to display in the view
	    model.addAttribute("user", currentUser)
    		.addAttribute("pageTitle", "DARET-ADMIN PROFILE");
	    return "profile";
	}

    /*---------------------------------------------------------------------------------------------------------------*/
    @PostMapping("/update-info")
    public String updateInfo(@ModelAttribute("user") User updatedUser, Model model) {
        // Retrieve the original user from the database
        User originalUser = userService.findById(updatedUser.getId());

        // Check if the email is being changed and if the new email already exists
        if (!originalUser.getEmail().equals(updatedUser.getEmail()) &&
                userService.existsByEmail(updatedUser.getEmail())) {
            model.addAttribute("ErrorMessage", "Email already exists. Please choose a different email.");
            return "redirect:/profile?error";
        } else if (!originalUser.getCin().equals(updatedUser.getCin()) &&
                userService.existsByCin(updatedUser.getCin())) {
            model.addAttribute("ErrorMessage", "CIN already exists. Please choose a different CIN.");
            return "redirect:/profile?error";
        }
        
            userService.updateUserInfo(updatedUser);
            model.addAttribute("SuccessMessage", "User information updated successfully!");
            return "redirect:/profile?success";
    }


    /*---------------------------------------------------------------------------------------------------------------*/
    @PostMapping("/update-password")
    public String updatePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Principal principal,
            Model model) {

        // Check if the principal is null
        if (principal == null || principal.getName() == null) {
            model.addAttribute("ErrorMessage", "User not authenticated. Password update failed.");
            return "redirect:/profile?error";
        }

        String userEmail = principal.getName();

        // Check if the current password is correct
        if (!userService.isCorrectPassword(userEmail, currentPassword)) {
            model.addAttribute("ErrorMessage", "Incorrect current password.");
            return "redirect:/profile?error";
        }

        // Check if the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("ErrorMessage", "New password and confirm password do not match.");
            return "redirect:/profile?error";
        }

        // Check if the new password is different from the old password
        if (userService.isCorrectPassword(userEmail, newPassword)) {
            model.addAttribute("ErrorMessage", "New password must be different from the current password.");
            return "redirect:/profile?error";
        }

        // Update user password
        userService.updateUserPassword(userEmail, currentPassword, newPassword);
        model.addAttribute("SuccessMessage", "Password updated successfully!");
        return "redirect:/profile?success";
    }

    /*---------------------------------------------------------------*/
    @PostMapping("/delete-account")
    public String deleteAccount(@ModelAttribute("user") User user, 
                                @RequestParam("password") String password,
                                Model model) {
        // Retrieve the original user from the database
        User existingUser = userService.findByEmail(user.getEmail());

        // Check if the user exists
        if (existingUser == null) {
            model.addAttribute("ErrorMessage", "User not found. Account deletion failed.");
            System.out.println("error 1");
            return "redirect:/profile?error";

        }

        // Check if the entered password is correct
        if (!userService.isCorrectPassword(existingUser.getEmail(), password)) {
            model.addAttribute("ErrorMessage", "Incorrect password. Account deletion failed.");
            System.out.println("error 2");
            return "redirect:/profile?error";
        }

        // Perform account deletion
        userService.deleteUser(existingUser.getEmail(), password);
        model.addAttribute("SuccessMessage", "Account deleted successfully!");
        return "redirect:/logout";
    }



}