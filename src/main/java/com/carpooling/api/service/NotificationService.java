package com.carpooling.api.service;

import com.carpooling.api.dto.NotificationDTO;
import com.carpooling.api.entity.Notification;
import com.carpooling.api.entity.User;
import com.carpooling.api.enums.NotificationType;
import com.carpooling.api.repository.NotificationRepository;
import com.carpooling.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;

    @Transactional
    public NotificationDTO createNotification(Long userId, String title, String message, 
                                             NotificationType type, Long referenceId,
                                             boolean sendEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        notification = notificationRepository.save(notification);
        
        NotificationDTO notificationDTO = mapToDTO(notification);
        messagingTemplate.convertAndSendToUser(
                userId.toString(), 
                "/topic/notifications", 
                notificationDTO
        );
        
        if (sendEmail && user.isEmailNotificationsEnabled()) {
            try {
                sendNotificationEmail(user, title, message, type, referenceId);
            } catch (MessagingException e) {
                System.err.println("Échec d'envoi d'email pour la notification: " + e.getMessage());
            }
        }
        
        return notificationDTO;
    }

    private void sendNotificationEmail(User user, String title, String message, 
                                      NotificationType type, Long referenceId) throws MessagingException {
        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("user", user);
        context.setVariable("title", title);
        context.setVariable("message", message);
        context.setVariable("notificationType", type.toString());
        context.setVariable("referenceId", referenceId);
        
        emailService.sendEmail(
            user.getEmail(),
            title,
            "notification",
            context
        );
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        
        notification.setRead(true);
        notification = notificationRepository.save(notification);
        
        return mapToDTO(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    public int countUnreadNotifications(Long userId) {
        return notificationRepository.countByUserIdAndRead(userId, false);
    }
    
    public void notifyBookingStatusChange(Long bookingId, Long userId, String status) {
        String title = "Statut de réservation mis à jour";
        String message = "Votre réservation a été " + status;
        NotificationType type = NotificationType.valueOf("BOOKING_" + status.toUpperCase());
        
        createNotification(userId, title, message, type, bookingId, true);
    }

    public void notifyUpcomingRide(Long rideId, Long userId, String rideInfo) {
        String title = "Rappel de trajet";
        String message = "Votre trajet " + rideInfo + " est prévu dans moins de 2 heures";
        
        createNotification(userId, title, message, NotificationType.RIDE_REMINDER, rideId, true);
    }
    
    public void notifyDriverPayment(Long bookingId, Long driverId, double amount, String passengerName) {
        String title = "Nouveau paiement reçu";
        String message = "Vous avez reçu un paiement de " + amount + "€ de la part de " + passengerName;
        
        createNotification(driverId, title, message, NotificationType.PAYMENT_RECEIVED, bookingId, true);
    }
    
    public void notifyPassengerPaymentConfirmation(Long bookingId, Long passengerId, double amount) {
        String title = "Paiement confirmé";
        String message = "Votre paiement de " + amount + "€ a été confirmé";
        
        createNotification(passengerId, title, message, NotificationType.PAYMENT_CONFIRMED, bookingId, true);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .referenceId(notification.getReferenceId())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}