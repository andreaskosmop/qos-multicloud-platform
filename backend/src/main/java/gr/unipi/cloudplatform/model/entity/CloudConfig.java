package gr.unipi.cloudplatform.model.entity;

import gr.unipi.cloudplatform.model.enums.CloudProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ρυθμίσεις ανάπτυξης μιας εφαρμογής σε συγκεκριμένο cloud provider
 * (instance type, εύρος κόμβων, region).
 */
@Entity
@Table(name = "cloud_configs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    private CloudProvider cloudProvider;   // AWS, AZURE

    @Column(nullable = false)
    private String instanceType;

    private int minNodes;
    private int maxNodes;
    private int desiredNodes;
    private String region;

    @Column(columnDefinition = "jsonb")
    private String portMappings;
}
