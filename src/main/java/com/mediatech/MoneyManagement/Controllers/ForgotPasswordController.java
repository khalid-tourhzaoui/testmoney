package com.mediatech.MoneyManagement.Controllers;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mediatech.MoneyManagement.Models.ForgotPasswordToken;
import com.mediatech.MoneyManagement.Models.User;
import com.mediatech.MoneyManagement.Repositories.ForgotPasswordRepository;
import com.mediatech.MoneyManagement.Services.ForgotPasswordService;
import com.mediatech.MoneyManagement.Services.UserService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPasswordController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ForgotPasswordService forgotPasswordService;
	
	@Autowired
	ForgotPasswordRepository forgotPasswordRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	
	@GetMapping("/password-request")
	public String passwordRequest() {
	
		return "Auth/password-request";
	}
	
	@PostMapping("/password-request")
	public String savePasswordRequest(@RequestParam("email") String email, Model model, RedirectAttributes redirectAttributes) {
	    User user = userService.findByEmail(email);
	    if (user == null) {
	        // Utilisez RedirectAttributes pour passer des messages entre les redirections
	        redirectAttributes.addFlashAttribute("errorMessage", "Cet e-mail n'est pas enregistré.");
	        return "redirect:/password-request";
	    }
	    
	    ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
	    forgotPasswordToken.setExpireTime(forgotPasswordService.expireTimeRange());
	    forgotPasswordToken.setToken(forgotPasswordService.generateToken());
	    forgotPasswordToken.setUser(user);
	    forgotPasswordToken.setUsed(false);
	    
	    forgotPasswordRepository.save(forgotPasswordToken);
	    
	    String emailLink = "http://localhost:2525/reset-password?token=" + forgotPasswordToken.getToken();
	    
	    try {
	        forgotPasswordService.sendEmail(user.getEmail(), "Lien de réinitialisation du mot de passe", emailLink);
	    } catch (UnsupportedEncodingException | MessagingException e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'envoi de l'e-mail.");
	        return "redirect:/password-request";
	    }
	    
	    // Utilisez RedirectAttributes pour passer des messages entre les redirections
	    redirectAttributes.addFlashAttribute("successMessage", "Le lien de réinitialisation du mot de passe a été envoyé avec succès.");
	    return "redirect:/password-request";
	}
	@GetMapping("/reset-password")
	public String resetPassword(@Param(value="token") String token, RedirectAttributes redirectAttributes, HttpSession session) {
		
		session.setAttribute("token", token);
		ForgotPasswordToken forgotPasswordToken = forgotPasswordRepository.findByToken(token);
		return forgotPasswordService.checkValidity(forgotPasswordToken, redirectAttributes);
		
	}
	@PostMapping("/reset-password")
	public String saveResetPassword(HttpServletRequest request, HttpSession session,RedirectAttributes redirectAttributes) {
	    try {
	        String password = request.getParameter("password");
	        String token = (String) session.getAttribute("token");

	        ForgotPasswordToken forgotPasswordToken = forgotPasswordRepository.findByToken(token);

	        User user = forgotPasswordToken.getUser();
	        if (password.length() < 8) {
	             redirectAttributes.addFlashAttribute("errorMessage", "Le mot de passe doit contenir au moins 8 caractères.");
	             return "redirect:/reset-password?token="+token;
	         }
	        user.setPassword(passwordEncoder.encode(password));
	        forgotPasswordToken.setUsed(true);
	        userService.save(user);
	        forgotPasswordRepository.save(forgotPasswordToken);

	        // Ajouter un attribut de message de succès dans le modèle
	        redirectAttributes.addFlashAttribute("successMessage", "Votre mot de passe a été réinitialisé avec succès.");
	        return "redirect:/login";
	    } catch (Exception e) {
	        // Gérer les autres exceptions
	    	redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors de la réinitialisation du mot de passe.");
	        return "redirect:/login?error";
	    }
	}


}
