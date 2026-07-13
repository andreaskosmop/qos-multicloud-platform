package gr.unipi.cloudplatform.repository;

import gr.unipi.cloudplatform.model.entity.Deployment;
import gr.unipi.cloudplatform.model.enums.DeploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeploymentRepository extends JpaRepository<Deployment, String> {
    List<Deployment> findByApplicationIdAndStatus(String applicationId, DeploymentStatus status);
}
