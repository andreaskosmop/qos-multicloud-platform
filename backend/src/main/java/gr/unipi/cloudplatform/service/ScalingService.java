package gr.unipi.cloudplatform.service;

import gr.unipi.cloudplatform.model.entity.Application;
import gr.unipi.cloudplatform.model.entity.QosRule;
import gr.unipi.cloudplatform.model.enums.CloudProvider;
import gr.unipi.cloudplatform.terraform.TerraformResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Εκτελεί τις πραγματικές ενέργειες κλιμάκωσης (scale nodes/pods)
 * που αποφασίζονται από τον MonitoringService κατά την παραβίαση
 * ενός QosRule.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ScalingService {

    private final TerraformService terraformService;

    public boolean scaleNodesUp(Application app, QosRule rule) {
        return scaleNodes(app, rule, +1);
    }

    public boolean scaleNodesDown(Application app, QosRule rule) {
        return scaleNodes(app, rule, -1);
    }

    private boolean scaleNodes(Application app, QosRule rule, int delta) {
        CloudProvider target = resolveTargetProvider(rule);
        int currentCount = app.getDeployments().stream()
                .filter(d -> d.getCloudProvider() == target)
                .findFirst()
                .map(d -> d.getNodeCount())
                .orElse(2);

        int newCount = Math.max(1, currentCount + delta);
        TerraformResult result = terraformService.scale(app.getId(), target, newCount);
        return result.isSuccess();
    }

    public boolean scalePodsUp(Application app, QosRule rule) {
        // Στο πραγματικό σύστημα: kubectl scale deployment ή ενημέρωση HPA min/max
        log.info("Scaling pods UP for app {}", app.getId());
        return true;
    }

    public boolean scalePodsDown(Application app, QosRule rule) {
        log.info("Scaling pods DOWN for app {}", app.getId());
        return true;
    }

    private CloudProvider resolveTargetProvider(QosRule rule) {
        if ("AWS".equalsIgnoreCase(rule.getActionTarget())) return CloudProvider.AWS;
        if ("AZURE".equalsIgnoreCase(rule.getActionTarget())) return CloudProvider.AZURE;
        return CloudProvider.AWS; // default fallback
    }
}
