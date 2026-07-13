package gr.unipi.cloudplatform.repository;

import gr.unipi.cloudplatform.model.entity.CloudConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudConfigRepository extends JpaRepository<CloudConfig, String> {
}
