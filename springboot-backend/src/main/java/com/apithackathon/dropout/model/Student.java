package com.apithackathon.dropout.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Student Entity - Represents a student with risk factors
 * Maps to database table and Kaggle dataset JSON
 */
@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_student_id", columnList = "studentId", unique = true),
    @Index(name = "idx_district", columnList = "district"),
    @Index(name = "idx_dropout_risk", columnList = "dropoutRisk")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false, unique = true, length = 50)
    @JsonProperty("id")
    private String studentId;
    
    @Column(nullable = false, length = 100)
    private String district;
    
    @Column(name = "attendance_rate", nullable = false)
    @JsonProperty("attendance_rate")
    private Double attendanceRate;
    
    @Column(name = "exam_score", nullable = false)
    @JsonProperty("exam_score")
    private Double examScore;
    
    @Column(name = "socio_economic_status", nullable = false, length = 20)
    @JsonProperty("socio_economic_status")
    private String socioEconomicStatus;
    
    @Column(name = "transport_allowance_used", nullable = false)
    @JsonProperty("transport_allowance_used")
    private Boolean transportAllowanceUsed;
    
    @Column(name = "migration_indicator", nullable = false)
    @JsonProperty("migration_indicator")
    private Integer migrationIndicator;
    
    @Column(nullable = false, length = 10)
    private String gender;
    
    @Column(name = "social_category", nullable = false, length = 20)
    @JsonProperty("social_category")
    private String socialCategory;
    
    @Column(name = "dropout_risk", nullable = false, length = 20)
    @JsonProperty("dropout_risk")
    private String dropoutRisk;
    
    @Column(name = "risk_probability")
    @JsonProperty("risk_probability")
    private Double riskProbability;
    
    @Column(name = "risk_score")
    @JsonProperty("risk_score")
    private Double riskScore;
    
    @Column(name = "predicted_label", length = 20)
    @JsonProperty("predicted_label")
    private String predictedLabel;
    
    // Transient fields (not stored in DB)
    @Transient
    @JsonProperty("reasons")
    private String[] reasons;
    
    /**
     * Calculate risk level based on probability
     */
    public String calculateRiskLevel() {
        if (riskProbability == null || riskScore == null) return "Unknown";
        
        double score = (riskProbability != null) ? riskProbability : riskScore;
        
        if (score >= 70.0) return "High";
        if (score >= 50.0) return "Moderate";
        return "Low";
    }
    
    /**
     * Check if student is at high risk
     */
    public boolean isHighRisk() {
        return "High".equalsIgnoreCase(dropoutRisk) || 
               (riskProbability != null && riskProbability >= 70.0);
    }
}
