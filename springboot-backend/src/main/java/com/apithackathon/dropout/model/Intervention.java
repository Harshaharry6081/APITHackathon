package com.apithackathon.dropout.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * Intervention Entity - Tracks interventions for at-risk students
 */
@Entity
@Table(name = "interventions", indexes = {
    @Index(name = "idx_intervention_student", columnList = "studentId"),
    @Index(name = "idx_intervention_date", columnList = "interventionDate"),
    @Index(name = "idx_intervention_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Intervention {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false, length = 50)
    @NotBlank(message = "Student ID is required")
    private String studentId;
    
    @Column(name = "intervention_type", nullable = false, length = 100)
    private String type;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "actioned_by", length = 100)
    private String actionedBy;
    
    @Column(length = 100)
    private String district;
    
    @Column(name = "risk_score")
    private Double riskScore;
    
    @Column(nullable = false, length = 50)
    private String status = "pending";
    
    @Column(name = "intervention_date", nullable = false)
    private LocalDateTime interventionDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (interventionDate == null) {
            interventionDate = LocalDateTime.now();
        }
        if (type == null || type.isEmpty()) {
            type = "Counseling";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
