package gr.unipi.cloudplatform.controller;

import gr.unipi.cloudplatform.dto.request.CreateAppRequest;
import gr.unipi.cloudplatform.model.entity.Application;
import gr.unipi.cloudplatform.model.entity.User;
import gr.unipi.cloudplatform.model.enums.CloudProvider;
import gr.unipi.cloudplatform.repository.ApplicationRepository;
import gr.unipi.cloudplatform.repository.CloudConfigRepository;
import gr.unipi.cloudplatform.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API resource-oriented endpoints (§4.4.4).
 * FA-A01, FA-A02, FA-A04, FA-A05, FA-A10, FA-A11.
 */
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationRepository applicationRepository;
    private final CloudConfigRepository cloudConfigRepository;

    @PostMapping
    public ResponseEntity<Application> create(@AuthenticationPrincipal User owner,
                                               @Valid @RequestBody CreateAppRequest req) {
        Application app = applicationService.create(owner, req.title(),
                req.description(), req.version(), req.tags());
        return ResponseEntity.status(HttpStatus.CREATED).body(app);
    }

    @GetMapping
    public ResponseEntity<List<Application>> list(@AuthenticationPrincipal User owner) {
        return ResponseEntity.ok(applicationService.findByOwner(owner.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Application> get(@PathVariable String id) {
        return applicationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** FA-A04: Διάταξη — POST /applications/{id}/deployment */
    @PostMapping("/{id}/deployment")
    public ResponseEntity<Void> deploy(@PathVariable String id,
                                        @RequestParam String cloudConfigId) {
        var cfg = cloudConfigRepository.findById(cloudConfigId)
                .orElseThrow(() -> new IllegalArgumentException("CloudConfig not found"));
        applicationService.deploy(id, cfg);
        return ResponseEntity.accepted().build();
    }

    /** FA-A05: Καταστροφή διάταξης — DELETE /applications/{id}/deployment */
    @DeleteMapping("/{id}/deployment")
    public ResponseEntity<Void> undeploy(@PathVariable String id,
                                          @RequestParam CloudProvider provider) {
        applicationService.undeploy(id, provider);
        return ResponseEntity.accepted().build();
    }

    /** Χειροκίνητη κλιμάκωση — PATCH /applications/{id}/deployment */
    @PatchMapping("/{id}/deployment")
    public ResponseEntity<Void> scale(@PathVariable String id,
                                       @RequestParam CloudProvider provider,
                                       @RequestParam int nodeCount) {
        // TerraformService.scale καλείται εδώ μέσω ApplicationService (παραλείπεται για συντομία)
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        applicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
