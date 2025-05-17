package com.carpooling.api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class WelcomeEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail; // Injection directe de la propriété

    public void sendWelcomeEmail(String email, String firstName) {
        log.info("Tentative d'envoi d'email à {} avec prénom {}", email, firstName);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Configuration du contexte pour le template
            Context context = new Context();
            context.setVariable("firstName", firstName);

            // Génération du contenu HTML
            String htmlContent = templateEngine.process("welcome-email", context);
            
            if (htmlContent == null || htmlContent.isEmpty()) {
                log.error("Le contenu HTML généré est vide");
                throw new RuntimeException("Le contenu HTML généré est vide");
            }

            // Configuration de l'email
            helper.setTo(email);
            helper.setSubject("Bienvenue sur TujeRide / Welcome to TujeRide");
            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail); // Utilisation de la propriété injectée

            // Envoi de l'email
            log.info("Envoi de l'email en cours...");
            mailSender.send(message);
            log.info("Email envoyé avec succès à {}", email);
        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email à {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email de bienvenue", e);
        }
    }
}