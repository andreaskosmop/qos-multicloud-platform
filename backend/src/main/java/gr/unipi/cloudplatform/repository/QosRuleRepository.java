package gr.unipi.cloudplatform.repository;

import gr.unipi.cloudplatform.model.entity.QosRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QosRuleRepository extends JpaRepository<QosRule, String> {
    List<QosRule> findByApplicationId(String applicationId);
}
