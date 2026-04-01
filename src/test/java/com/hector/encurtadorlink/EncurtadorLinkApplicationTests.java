package com.hector.encurtadorlink;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("EncurtadorLink Application Integration Tests")
@SpringBootTest
@Disabled("Integration test requires running PostgreSQL and Redis")
class EncurtadorLinkApplicationTests {

    @Test
    @DisplayName("Application context should load successfully")
    void contextLoads() {
        // This test requires a running database and is skipped by default
        // Enable by removing @Disabled and starting PostgreSQL + Redis
    }
}
