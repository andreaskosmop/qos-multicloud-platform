package gr.unipi.cloudplatform.repository;

import gr.unipi.cloudplatform.model.entity.ScalingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScalingEventRepository extends JpaRepository<ScalingEvent, String> {
}
