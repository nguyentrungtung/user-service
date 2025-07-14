package service.com.service.userservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Disabled;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Temporarily disabled due to migration conflict")
public class UserServiceTest {
    
    @Test
    void contextLoads() {
        // This test ensures that the Spring context loads properly
    }
}
