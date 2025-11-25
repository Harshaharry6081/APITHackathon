package com.apithackathon.dropout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Risk Score DTO - Risk assessment result for a student
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskScore {
    private String studentId;
    private String riskLevel;
    private Double riskProbability;
    private Double riskScore;
    private List<String> reasons;
    private String district;
    private StudentDetails studentDetails;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentDetails {
        private Double attendanceRate;
        private Double examScore;
        private String socioEconomicStatus;
        private Boolean transportAllowanceUsed;
        private Integer migrationIndicator;
        private String gender;
        private String socialCategory;
    }
}
