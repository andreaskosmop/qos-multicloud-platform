package gr.unipi.cloudplatform.repository;

import gr.unipi.cloudplatform.model.entity.Application;
import gr.unipi.cloudplatform.model.enums.AppStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, String> {
    List<Application> findByStatus(AppStatus status);
    List<Application> findByOwnerId(String ownerId);
}
