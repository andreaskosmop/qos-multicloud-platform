package gr.unipi.cloudplatform.service;

import gr.unipi.cloudplatform.model.entity.Application;
import gr.unipi.cloudplatform.model.entity.QosRule;
import gr.unipi.cloudplatform.model.entity.ScalingEvent;
import gr.unipi.cloudplatform.model.enums.AppStatus;
import gr.unipi.cloudplatform.model.enums.ScalingEventStatus;
import gr.unipi.cloudplatform.repository.ApplicationRepository;
import gr.unipi.cloudplatform.repository.ScalingEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Ο MonitoringService εκτελεί τον κεντρικό έλεγχο QoS κανόνων κάθε 30 δευτερόλεπτα.
 * Χρησιμοποιεί @Scheduled annotation του Spring Boot και Java 17 switch expressions
 * για routing ενεργειών.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MonitoringService {

    private final PrometheusService prometheusService;
    private final ApplicationRepository appRepository;
    private final ScalingService scalingService;
    private final NotificationService notificationService;
    private final ScalingEventRepository eventRepository;

    @Scheduled(fixedDelay = 30000)  // κάθε 30 δευτερόλεπτα
    public void evaluateAllApplications() {
        appRepository.findByStatus(AppStatus.DEPLOYED)
                     .forEach(this::evaluateApplication);
    }

    void evaluateApplication(Application app) {
        for (QosRule rule : app.getQosRules()) {
            if (!rule.isActive()) continue;
            try {
                Double value = prometheusService
                        .queryMetric(app.getId(), rule.getMetricName());
                if (value == null) continue;

                boolean violated = evaluate(value, rule.getOperator(),
                        rule.getThresholdValue());
                if (violated) {
                    log.warn("QoS violation: app={} metric={} value={} threshold={}",
                            app.getId(), rule.getMetricName(),
                            value, rule.getThresholdValue());
                    handleViolation(app, rule, value);
                }
            } catch (Exception e) {
                log.error("Error evaluating rule {}", rule.getId(), e);
            }
        }
    }

    private void handleViolation(Application app, QosRule rule, Double val) {
        ScalingEvent event = ScalingEvent.builder()
                .application(app)
                .rule(rule)
                .triggeredAt(LocalDateTime.now())
                .actionType(rule.getActionType())
                .actionStatus(ScalingEventStatus.IN_PROGRESS)
                .metricValue(val)
                .build();
        eventRepository.save(event);

        boolean ok = switch (rule.getActionType()) {
            case SCALE_NODES_UP   -> scalingService.scaleNodesUp(app, rule);
            case SCALE_NODES_DOWN -> scalingService.scaleNodesDown(app, rule);
            case SCALE_PODS_UP    -> scalingService.scalePodsUp(app, rule);
            case SCALE_PODS_DOWN  -> scalingService.scalePodsDown(app, rule);
            case NOTIFY -> {
                notificationService.notifyQosViolation(app, rule, val);
                yield true;
            }
        };
        event.setActionStatus(ok ? ScalingEventStatus.SUCCESS : ScalingEventStatus.FAILED);
        eventRepository.save(event);
    }

    private boolean evaluate(Double val, String op, Double threshold) {
        return switch (op) {
            case ">"  -> val > threshold;
            case "<"  -> val < threshold;
            case ">=" -> val >= threshold;
            case "<=" -> val <= threshold;
            case "==" -> val.equals(threshold);
            default   -> false;
        };
    }
}
