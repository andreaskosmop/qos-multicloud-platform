package gr.unipi.cloudplatform.service;

import gr.unipi.cloudplatform.model.entity.Application;
import gr.unipi.cloudplatform.model.entity.QosRule;
import gr.unipi.cloudplatform.model.enums.ActionType;
import gr.unipi.cloudplatform.repository.ApplicationRepository;
import gr.unipi.cloudplatform.repository.ScalingEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit test παράδειγμα (Παράρτημα ΣΤ.1) — δοκιμάζει τη λογική αξιολόγησης
 * QoS κανόνων του MonitoringService, χωρίς πραγματική σύνδεση σε
 * Prometheus/DB (χρήση Mockito mocks για όλα τα εξαρτημένα components).
 */
@ExtendWith(MockitoExtension.class)
class MonitoringServiceTest {

    @Mock private PrometheusService prometheusService;
    @Mock private ApplicationRepository appRepository;
    @Mock private ScalingService scalingService;
    @Mock private NotificationService notificationService;
    @Mock private ScalingEventRepository eventRepository;

    @InjectMocks
    private MonitoringService monitoringService;

    private Application app;
    private QosRule rule;

    @BeforeEach
    void setUp() {
        rule = QosRule.builder()
                .metricName("cpu_usage")
                .operator(">")
                .thresholdValue(80.0)
                .actionType(ActionType.SCALE_NODES_UP)
                .isActive(true)
                .build();

        app = Application.builder()
                .id("app-123")
                .qosRules(List.of(rule))
                .build();
    }

    @Test
    @DisplayName("Όταν η μετρική υπερβαίνει το threshold, ενεργοποιείται scale-up")
    void whenCpuExceedsThreshold_thenTriggersScaleUp() {
        when(prometheusService.queryMetric("app-123", "cpu_usage")).thenReturn(85.0);
        when(scalingService.scaleNodesUp(app, rule)).thenReturn(true);

        monitoringService.evaluateApplication(app);

        verify(scalingService, times(1)).scaleNodesUp(app, rule);
        verify(eventRepository, times(2)).save(any());  // IN_PROGRESS + SUCCESS
    }

    @Test
    @DisplayName("Όταν η μετρική είναι εντός ορίων, δεν ενεργοποιείται καμία ενέργεια")
    void whenCpuWithinThreshold_thenNoActionTriggered() {
        when(prometheusService.queryMetric("app-123", "cpu_usage")).thenReturn(50.0);

        monitoringService.evaluateApplication(app);

        verify(scalingService, never()).scaleNodesUp(any(), any());
        verify(eventRepository, never()).save(any());
    }
}
