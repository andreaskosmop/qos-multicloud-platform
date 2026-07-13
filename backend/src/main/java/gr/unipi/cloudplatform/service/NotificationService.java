package gr.unipi.cloudplatform.service;

import gr.unipi.cloudplatform.model.entity.Application;
import gr.unipi.cloudplatform.model.entity.Notification;
import gr.unipi.cloudplatform.model.entity.QosRule;
import gr.unipi.cloudplatform.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Αποθήκευση ειδοποιήσεων στη βάση δεδομένων (in-app) + αποστολή email μέσω SMTP.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public void notifyQosViolation(Application app, QosRule rule, Double value) {
        String message = "Ο κανόνας QoS '%s' παραβιάστηκε (τιμή: %.2f, όριο: %.2f)"
                .formatted(rule.getMetricName(), value, rule.getThresholdValue());

        Notification notification = Notification.builder()
                .user(app.getOwner())
                .application(app)
                .type("QOS_VIOLATION")
                .title("Παραβίαση Κανόνα QoS")
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        sendEmail(app.getOwner().getEmail(), "Παραβίαση Κανόνα QoS", message);
    }

    public void notifyDeploymentResult(Application app, boolean success) {
        String type = success ? "DEPLOYMENT_SUCCESS" : "DEPLOYMENT_FAILED";
        String title = success ? "Επιτυχής Διάταξη" : "Αποτυχία Διάταξης";
        String message = "Η εφαρμογή '%s' %s".formatted(
                app.getTitle(), success ? "διατάχθηκε επιτυχώς." : "απέτυχε να διαταχθεί.");

        Notification notification = Notification.builder()
                .user(app.getOwner())
                .application(app)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        sendEmail(app.getOwner().getEmail(), title, message);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);
        } catch (Exception e) {
            log.warn("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
