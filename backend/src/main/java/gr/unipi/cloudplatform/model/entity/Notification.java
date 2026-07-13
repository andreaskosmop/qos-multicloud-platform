package gr.unipi.cloudplatform.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Ειδοποίηση προς χρήστη (in-app + email).
 * Το πεδίο isRead αφορά αποκλειστικά τις in-app ειδοποιήσεις.
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    private Application application;

    @Column(nullable = false)
    private String type;   // QOS_VIOLATION, DEPLOYMENT_SUCCESS, DEPLOYMENT_FAILED, ACCOUNT_ACTIVATED

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    private boolean isRead;
    private LocalDateTime createdAt;
}
