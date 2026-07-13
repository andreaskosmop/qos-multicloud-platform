package gr.unipi.cloudplatform.model.entity;

import gr.unipi.cloudplatform.model.enums.ActionType;
import gr.unipi.cloudplatform.model.enums.ScalingEventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Ιστορικό κλιμάκωσης — καταγράφει κάθε φορά που ένας QosRule
 * παραβιάστηκε και ποια ενέργεια εκτελέστηκε.
 */
@Entity
@Table(name = "scaling_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScalingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qos_rule_id")
    private QosRule rule;

    private LocalDateTime triggeredAt;
    private Double metricValue;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    private ScalingEventStatus actionStatus;   // IN_PROGRESS/SUCCESS/FAILED

    private String errorMessage;

    public void setActionStatus(ScalingEventStatus status) {
        this.actionStatus = status;
    }
}
