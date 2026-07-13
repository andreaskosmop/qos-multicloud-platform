package gr.unipi.cloudplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // απαραίτητο για το @Scheduled του MonitoringService
public class CloudPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudPlatformApplication.class, args);
    }
}
