package com.mediatech.MoneyManagement.Services;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import com.mediatech.MoneyManagement.Models.ForgotPasswordToken;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ForgotPasswordService {

    @Autowired
    JavaMailSender javaMailSender;

    // Durée de validité du jeton en minutes
    private final int MINUTES = 5;

    // Génère un jeton UUID
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    // Calcule le moment d'expiration du jeton
    public LocalDateTime expireTimeRange() {
        return LocalDateTime.now().plusMinutes(MINUTES);
    }

    // Envoie un e-mail avec un lien pour réinitialiser le mot de passe
    public void sendEmail(String to, String subject, String emailLink) throws MessagingException, UnsupportedEncodingException {
    	// Crée un objet MimeMessage à l'aide du JavaMailSender
    	MimeMessage message = javaMailSender.createMimeMessage();

    	// Initialise un objet MimeMessageHelper pour faciliter la configuration de l'objet MimeMessage
    	MimeMessageHelper helper = new MimeMessageHelper(message);

        String emailContent = "<p>Hello</p>"
                              + "Click the link the below to reset password"
                              + "<p><a href=\"" + emailLink + "\">Change My Password</a></p>"
                              + "<br>"
                              + "Ignore this Email if you did not made the request";
        helper.setText(emailContent, true);
        helper.setFrom("DARETMANAGEMENT@gmail.com", "Daret Management Support");
        helper.setSubject(subject);
        helper.setTo(to);
        javaMailSender.send(message);
    }

    // Vérifie si un jeton est expiré
    public boolean isExpired(ForgotPasswordToken forgotPasswordToken) {
        return LocalDateTime.now().isAfter(forgotPasswordToken.getExpireTime());
    }

    // Vérifie la validité du jeton et renvoie le nom de la vue approprié
    public String checkValidity(ForgotPasswordToken forgotPasswordToken, Model model) {
        if (forgotPasswordToken == null) {
            model.addAttribute("error", "Invalid Token");
            return "Auth/login";
        } else if (forgotPasswordToken.isUsed()) {
            model.addAttribute("error", "The token is already used");
            return "Auth/login";
        } else if (isExpired(forgotPasswordToken)) {
            model.addAttribute("error", "The token is expired");
            return "Auth/login";
        } else {
            return "Auth/reset-password";
        }
    }
}
