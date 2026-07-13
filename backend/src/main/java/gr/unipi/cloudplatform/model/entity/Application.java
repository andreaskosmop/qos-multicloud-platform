package gr.unipi.cloudplatform.model.entity;

import gr.unipi.cloudplatform.model.enums.AppStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Το κεντρικό domain object.
 * Χρησιμοποιεί CascadeType.ALL στις σχέσεις με CloudConfig και QosRule,
 * εξασφαλίζοντας αυτόματη cascade delete.
 * Η κατάσταση (AppStatus) ακολουθεί το lifecycle:
 * INITIAL → DEPLOYING → DEPLOYED → UNDEPLOYING → TERMINATED / FAILED
 */
@Entity
@Table(name = "applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;
    private String description;
    private String version;

    @ElementCollection
    private List<String> tags;

    @Enumerated(EnumType.STRING)
    private AppStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "application",
               cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CloudConfig> cloudConfigs;

    @OneToMany(mappedBy = "application",
               cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QosRule> qosRules;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    private List<Deployment> deployments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
