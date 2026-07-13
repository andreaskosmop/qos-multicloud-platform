package gr.unipi.cloudplatform.service;

import gr.unipi.cloudplatform.model.entity.Application;
import gr.unipi.cloudplatform.model.entity.CloudConfig;
import gr.unipi.cloudplatform.model.entity.Deployment;
import gr.unipi.cloudplatform.model.entity.User;
import gr.unipi.cloudplatform.model.enums.AppStatus;
import gr.unipi.cloudplatform.model.enums.CloudProvider;
import gr.unipi.cloudplatform.model.enums.DeploymentStatus;
import gr.unipi.cloudplatform.repository.ApplicationRepository;
import gr.unipi.cloudplatform.repository.DeploymentRepository;
import gr.unipi.cloudplatform.terraform.TerraformResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FA-A01 (Δημιουργία), FA-A04 (Διάταξη), FA-A05 (Αποδιάταξη/Καταστροφή),
 * FA-A10 (Διαγραφή), FA-A11 (Λίστα εφαρμογών).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final DeploymentRepository deploymentRepository;
    private final TerraformService terraformService;
    private final NotificationService notificationService;

    public Application create(User owner, String title, String description,
                               String version, List<String> tags) {
        Application app = Application.builder()
                .title(title)
                .description(description)
                .version(version)
                .tags(tags)
                .status(AppStatus.INITIAL)
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build();
        return applicationRepository.save(app);
    }

    /** FA-A04: Διάταξη εφαρμογής σε επιλεγμένο νέφος. */
    public void deploy(String appId, CloudConfig cfg) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        app.setStatus(AppStatus.DEPLOYING);
        applicationRepository.save(app);

        TerraformResult result = terraformService.deploy(app, cfg);

        if (result.isSuccess()) {
            Deployment deployment = Deployment.builder()
                    .application(app)
                    .cloudProvider(cfg.getCloudProvider())
                    .nodeCount(cfg.getDesiredNodes())
                    .status(DeploymentStatus.ACTIVE)
                    .deployedAt(LocalDateTime.now())
                    .build();
            deploymentRepository.save(deployment);
            app.setStatus(AppStatus.DEPLOYED);
        } else {
            app.setStatus(AppStatus.FAILED);
            log.error("Deployment failed: {}", result.getErrorMessage());
        }
        app.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(app);
        notificationService.notifyDeploymentResult(app, result.isSuccess());
    }

    /** FA-A05: Καταστροφή διάταξης (destroy), όχι αναδιάταξη. */
    public void undeploy(String appId, CloudProvider provider) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        app.setStatus(AppStatus.UNDEPLOYING);
        applicationRepository.save(app);

        TerraformResult result = terraformService.destroy(appId, provider);

        deploymentRepository.findByApplicationIdAndStatus(appId, DeploymentStatus.ACTIVE)
                .forEach(d -> {
                    d.setStatus(DeploymentStatus.TERMINATED);
                    d.setTerminatedAt(LocalDateTime.now());
                    deploymentRepository.save(d);
                });

        app.setStatus(result.isSuccess() ? AppStatus.TERMINATED : AppStatus.FAILED);
        app.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(app);
    }

    /** FA-A10: Διαγραφή εφαρμογής — αν είναι διατεταγμένη, καταστρέφεται πρώτα η υποδομή. */
    public void delete(String appId) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (app.getStatus() == AppStatus.DEPLOYED) {
            app.getDeployments().forEach(d ->
                    terraformService.destroy(appId, d.getCloudProvider()));
        }
        applicationRepository.delete(app);  // cascade delete στα CloudConfig/QosRule/Deployment
    }

    public List<Application> findByOwner(String ownerId) {
        return applicationRepository.findByOwnerId(ownerId);
    }
}
