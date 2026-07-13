package gr.unipi.cloudplatform.model.entity;

import gr.unipi.cloudplatform.model.enums.ActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ορισμός Κανόνα QoS.
 * Κάθε QosRule συνδέεται με μία εφαρμογή (ManyToOne) και ορίζει:
 * την παρακολουθούμενη μετρική, τον τελεστή σύγκρισης, το threshold
 * και την ενέργεια που εκτελείται κατά παραβίαση.
 */
@Entity
@Table(name = "qos_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QosRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = false)
    private Application application;

    @Column(nullable = false)
    private String metricName;     // "cpu_usage", "response_time", custom

    @Column(nullable = false)
    private String operator;       // ">", "<", ">=", "<="

    @Column(nullable = false)
    private Double thresholdValue;

    private Integer durationSeconds;  // Διάρκεια παραβίασης πριν την ενεργοποίηση

    @Enumerated(EnumType.STRING)
    private ActionType actionType; // SCALE_NODES_UP/DOWN, SCALE_PODS_UP/DOWN, NOTIFY

    private Double actionValue;    // π.χ. +2 nodes, +30% pods
    private String actionTarget;   // "AWS", "AZURE", "all"
    private Integer cooldownSeconds;
    private boolean isActive;
}
