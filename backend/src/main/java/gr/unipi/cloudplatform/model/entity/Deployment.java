package gr.unipi.cloudplatform.model.entity;

import gr.unipi.cloudplatform.model.enums.CloudProvider;
import gr.unipi.cloudplatform.model.enums.DeploymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Ενεργή διάταξη μιας εφαρμογής σε συγκεκριμένο cloud provider.
 * Καταγράφει το cluster endpoint, το prometheus endpoint και την
 * κατάσταση της υποδομής.
 */
@Entity
@Table(name = "deployments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deployment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    private CloudProvider cloudProvider;

    private String clusterEndpoint;
    private String prometheusEndpoint;
    private int nodeCount;

    @Enumerated(EnumType.STRING)
    private DeploymentStatus status;   // PROVISIONING/ACTIVE/FAILED/TERMINATED

    private String terraformWorkspace;
    private LocalDateTime deployedAt;
    private LocalDateTime terminatedAt;
}
