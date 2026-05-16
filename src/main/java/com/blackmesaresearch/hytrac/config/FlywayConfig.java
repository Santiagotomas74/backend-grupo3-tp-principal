package com.blackmesaresearch.hytrac.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            try {
                // Try to migrate normally. 
                // If you touched V1, this will throw an exception.
                flyway.migrate();
            } catch (Exception e) {
                // This block catches the "Checksum mismatch" or "SQL Error"
                System.err.println(">> Flyway Migration Failed: " + e.getMessage());
                System.err.println(">> Action: Nuking SQLite database to sync schema changes...");
                
                // Manually trigger the clean and re-migrate
                flyway.clean();
                flyway.migrate();
            }
        };
    }
}