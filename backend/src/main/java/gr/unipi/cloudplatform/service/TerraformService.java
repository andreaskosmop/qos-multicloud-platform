package gr.unipi.cloudplatform.service;

import gr.unipi.cloudplatform.model.entity.Application;
import gr.unipi.cloudplatform.model.entity.CloudConfig;
import gr.unipi.cloudplatform.model.enums.CloudProvider;
import gr.unipi.cloudplatform.terraform.TerraformResult;
import gr.unipi.cloudplatform.terraform.TerraformTemplateGenerator;
import gr.unipi.cloudplatform.exception.DeploymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Ο TerraformService είναι η πιο κρίσιμη service class.
 * Χρησιμοποιεί Java ProcessBuilder για εκτέλεση Terraform commands σε subprocess.
 * Κάθε εφαρμογή έχει δικό της workspace directory ανά cloud provider.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TerraformService {

    @Value("${terraform.workdir}")
    private String terraformWorkdir;

    @Value("${terraform.binary.path}")
    private String terraformBinaryPath;

    private final TerraformTemplateGenerator templateGenerator;

    public TerraformResult deploy(Application app, CloudConfig cfg) {
        String workspace = buildWorkspacePath(app.getId(), cfg.getCloudProvider());
        try {
            templateGenerator.generate(app, cfg, workspace);
            executeCommand(workspace, "init");
            executeCommand(workspace, "plan", "-out=tfplan");
            executeCommand(workspace, "apply", "-auto-approve", "tfplan");
            String outputs = executeCommand(workspace, "output", "-json");
            return TerraformResult.success(parseOutputs(outputs));
        } catch (Exception e) {
            log.error("Deploy failed for app {} on {}", app.getId(), cfg.getCloudProvider(), e);
            return TerraformResult.failure(e.getMessage());
        }
    }

    public TerraformResult destroy(String appId, CloudProvider provider) {
        String workspace = buildWorkspacePath(appId, provider);
        try {
            executeCommand(workspace, "destroy", "-auto-approve");
            cleanupWorkspace(workspace);
            return TerraformResult.success(Map.of());
        } catch (Exception e) {
            return TerraformResult.failure(e.getMessage());
        }
    }

    public TerraformResult scale(String appId, CloudProvider provider, int count) {
        String workspace = buildWorkspacePath(appId, provider);
        try {
            executeCommand(workspace, "apply", "-auto-approve",
                    "-var", "desired_nodes=" + count);
            return TerraformResult.success(Map.of());
        } catch (Exception e) {
            return TerraformResult.failure("Scale failed: " + e.getMessage());
        }
    }

    private String executeCommand(String workdir, String... args)
            throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        cmd.add(terraformBinaryPath);
        cmd.addAll(Arrays.asList(args));
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(new File(workdir));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String output = new String(process.getInputStream().readAllBytes());
        int exit = process.waitFor();
        if (exit != 0) {
            throw new DeploymentException("Terraform failed: " + output);
        }
        return output;
    }

    private void cleanupWorkspace(String workspace) {
        // Διαγραφή του terraform workspace directory μετά επιτυχή destroy
        File dir = new File(workspace);
        if (dir.exists()) {
            deleteRecursively(dir);
        }
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }

    private Map<String, Object> parseOutputs(String jsonOutputs) {
        // TODO: parse με Jackson ObjectMapper τα terraform output -json αποτελέσματα
        return Map.of("raw", jsonOutputs);
    }

    private String buildWorkspacePath(String appId, CloudProvider p) {
        return terraformWorkdir + "/" + appId + "/" + p.name().toLowerCase();
    }
}
