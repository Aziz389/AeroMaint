package com.aeromaint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point of the AeroMaint backend.
 * On startup Spring Boot connects to the database, Flyway runs the migrations,
 * then Hibernate validates that the JPA entities match the tables.
 * @EnableScheduling turns on @Scheduled jobs (preventive maintenance).
 */
@SpringBootApplication
@EnableScheduling
public class AeroMaintApplication {

    public static void main(String[] args) {
        SpringApplication.run(AeroMaintApplication.class, args);
    }
}
