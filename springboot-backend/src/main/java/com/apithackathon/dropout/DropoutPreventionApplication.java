package com.apithackathon.dropout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main Spring Boot Application
 * AI-Assisted Early Warning System for Student Dropout Prevention
 * APIT Hackathon 2025 - Andhra Pradesh School Education Department
 */
@SpringBootApplication
@EnableCaching
public class DropoutPreventionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DropoutPreventionApplication.class, args);
        
        System.out.println("\n" +
            "╔════════════════════════════════════════════════════════════════╗\n" +
            "║   🎓 AI Dropout Prevention System - Spring Boot Backend       ║\n" +
            "║   APIT Hackathon 2025 - Andhra Pradesh                        ║\n" +
            "║                                                                ║\n" +
            "║   ✅ Server running on http://localhost:8080                  ║\n" +
            "║   ✅ API endpoints: /api/students, /api/interventions         ║\n" +
            "║   ✅ Health check: http://localhost:8080/actuator/health      ║\n" +
            "║   ✅ Swagger UI (if configured): /swagger-ui.html             ║\n" +
            "║                                                                ║\n" +
            "║   📊 4,424 students loaded from Kaggle dataset                ║\n" +
            "║   🎯 Ready for demo!                                          ║\n" +
            "╚════════════════════════════════════════════════════════════════╝\n"
        );
    }
}
