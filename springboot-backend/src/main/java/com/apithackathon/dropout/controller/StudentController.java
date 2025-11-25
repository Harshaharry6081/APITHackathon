package com.apithackathon.dropout.controller;

import com.apithackathon.dropout.model.*;
import com.apithackathon.dropout.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Student Controller - REST API endpoints for student dropout prevention system
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StudentController {
    
    private final StudentService studentService;
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
    
    /**
     * Get student by ID
     */
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable String id) {
        log.info("Getting student: {}", id);
        return studentService.getStudentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get risk score for student
     */
    @GetMapping("/students/{id}/risk")
    public ResponseEntity<RiskScore> getStudentRisk(@PathVariable String id) {
        log.info("Getting risk score for student: {}", id);
        try {
            RiskScore riskScore = studentService.getRiskScore(id);
            return ResponseEntity.ok(riskScore);
        } catch (RuntimeException e) {
            log.error("Student not found: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all at-risk students
     */
    @GetMapping("/students/at-risk/all")
    public ResponseEntity<List<Student>> getAtRiskStudents(
            @RequestParam(required = false, defaultValue = "50.0") Double threshold) {
        log.info("Getting at-risk students with threshold: {}", threshold);
        List<Student> students = studentService.getAtRiskStudents(threshold);
        return ResponseEntity.ok(students);
    }
    
    /**
     * Get students by district
     */
    @GetMapping("/students/district/{district}")
    public ResponseEntity<List<Student>> getStudentsByDistrict(@PathVariable String district) {
        log.info("Getting students for district: {}", district);
        List<Student> students = studentService.getStudentsByDistrict(district);
        return ResponseEntity.ok(students);
    }
    
    /**
     * Get district statistics
     */
    @GetMapping("/districts/{name}/stats")
    public ResponseEntity<DistrictStats> getDistrictStats(@PathVariable String name) {
        log.info("Getting statistics for district: {}", name);
        DistrictStats stats = studentService.getDistrictStats(name);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get all districts
     */
    @GetMapping("/districts")
    public ResponseEntity<List<String>> getAllDistricts() {
        log.info("Getting all districts");
        List<String> districts = studentService.getAllDistricts();
        return ResponseEntity.ok(districts);
    }
    
    /**
     * Log intervention
     */
    @PostMapping("/interventions")
    public ResponseEntity<Map<String, Object>> logIntervention(@Valid @RequestBody Intervention intervention) {
        log.info("Logging intervention for student: {}", intervention.getStudentId());
        Intervention saved = studentService.logIntervention(intervention);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "message", "Intervention logged successfully",
            "intervention", saved
        ));
    }
    
    /**
     * Get interventions
     */
    @GetMapping("/interventions")
    public ResponseEntity<Map<String, Object>> getInterventions(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        log.info("Getting interventions - studentId: {}, limit: {}", studentId, limit);
        Map<String, Object> result = studentService.getInterventions(studentId, limit);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get intervention by ID
     */
    @GetMapping("/interventions/{id}")
    public ResponseEntity<Intervention> getInterventionById(@PathVariable Long id) {
        log.info("Getting intervention: {}", id);
        return studentService.getInterventionById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get model metrics
     */
    @GetMapping("/model/metrics")
    public ResponseEntity<ModelMetrics> getModelMetrics() {
        log.info("Getting model metrics");
        ModelMetrics metrics = studentService.getModelMetrics();
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * LEAP Integration - Field worker update endpoint
     */
    @PostMapping("/leap/field-update")
    public ResponseEntity<Map<String, Object>> leapFieldUpdate(@RequestBody Map<String, Object> payload) {
        log.info("LEAP field update received: {}", payload);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Field update received from LEAP mobile app",
            "data", payload,
            "timestamp", System.currentTimeMillis()
        ));
    }
    
    /**
     * Exception handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Error occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "error", e.getMessage(),
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}
