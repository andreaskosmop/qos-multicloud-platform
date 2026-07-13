package gr.unipi.cloudplatform.terraform;

import gr.unipi.cloudplatform.model.entity.Application;
import gr.unipi.cloudplatform.model.entity.CloudConfig;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Δημιουργεί δυναμικά τα .tf αρχεία (main.tf, variables.tf, outputs.tf)
 * για μια συγκεκριμένη εφαρμογή + cloud config, βάσει templates
 * (βλ. Παράρτημα Γ για AWS EKS, Παράρτημα Δ για Azure AKS).
 *
 * Στην πλήρη υλοποίηση θα χρησιμοποιούσε μηχανή templating
 * (π.χ. Freemarker/Thymeleaf) — εδώ δίνεται απλοποιημένη εκδοχή.
 */
@Component
public class TerraformTemplateGenerator {

    public void generate(Application app, CloudConfig cfg, String workspacePath) throws IOException {
        Path dir = Path.of(workspacePath);
        Files.createDirectories(dir);

        String mainTf = switch (cfg.getCloudProvider()) {
            case AWS -> buildAwsEksTemplate(app, cfg);
            case AZURE -> buildAzureAksTemplate(app, cfg);
        };

        Files.writeString(dir.resolve("main.tf"), mainTf);
    }

    private String buildAwsEksTemplate(Application app, CloudConfig cfg) {
        // Βλ. Παράρτημα Γ: Terraform Module — AWS EKS για το πλήρες template
        return """
                module "eks" {
                  source          = "terraform-aws-modules/eks/aws"
                  cluster_name    = "%s"
                  cluster_version = "1.28"
                  instance_types  = ["%s"]
                  min_size        = %d
                  max_size        = %d
                  desired_size    = %d
                }
                """.formatted(app.getId(), cfg.getInstanceType(),
                cfg.getMinNodes(), cfg.getMaxNodes(), cfg.getDesiredNodes());
    }

    private String buildAzureAksTemplate(Application app, CloudConfig cfg) {
        // Βλ. Παράρτημα Δ: Terraform Module — Azure AKS για το πλήρες template
        return """
                resource "azurerm_kubernetes_cluster" "main" {
                  name       = "%s"
                  location   = "%s"
                  vm_size    = "%s"
                }
                """.formatted(app.getId(), cfg.getRegion(), cfg.getInstanceType());
    }
}
