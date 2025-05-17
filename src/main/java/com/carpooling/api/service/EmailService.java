package com.carpooling.api.service;

import com.carpooling.api.entity.Booking;
import com.carpooling.api.entity.User;
import com.carpooling.api.entity.Passenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${application.admin.email}")
    private String adminEmail;

    public void sendPaymentConfirmationEmail(Booking booking) throws MessagingException {
        Passenger passenger = booking.getPassenger();
        User passengerUser = passenger.getUser();
        User driver = booking.getRide().getDriver().getUser();
        double amount = booking.getRide().getPricePerSeat().doubleValue() * booking.getSeats();

        sendEmail(
            passengerUser.getEmail(),
            "Confirmation de paiement - Carpooling",
            "payment-confirmation",
            createPaymentContext(passengerUser, driver, booking, amount)
        );

        sendEmail(
            driver.getEmail(),
            "Nouveau paiement reçu - Carpooling",
            "payment-notification-driver",
            createPaymentContext(passengerUser, driver, booking, amount)
        );

        sendEmail(
            adminEmail,
            "Nouvelle transaction - Carpooling",
            "payment-notification-admin",
            createPaymentContext(passengerUser, driver, booking, amount)
        );
    }

    public void sendNewUserNotification(User user) throws MessagingException {
        sendEmail(
            user.getEmail(),
            "Bienvenue sur Carpooling",
            "welcome",
            createWelcomeContext(user)
        );

        sendEmail(
            adminEmail,
            "Nouvel utilisateur inscrit",
            "new-user-notification",
            createNewUserContext(user)
        );
    }

    public void sendEmail(String to, String subject, String templateName, Context context) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject(subject);
        
        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true);
        
        emailSender.send(message);
    }

    private Context createPaymentContext(User passenger, User driver, Booking booking, double amount) {
        Context context = new Context(Locale.FRENCH);
        context.setVariable("passenger", passenger);
        context.setVariable("driver", driver);
        context.setVariable("booking", booking);
        context.setVariable("amount", amount);
        context.setVariable("ride", booking.getRide());
        return context;
    }

    private Context createWelcomeContext(User user) {
        Context context = new Context(Locale.FRENCH);
        context.setVariable("user", user);
        return context;
    }

    private Context createNewUserContext(User user) {
        Context context = new Context(Locale.FRENCH);
        context.setVariable("user", user);
        return context;
    }
} 