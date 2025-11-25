package com.apithackathon.dropout.service;

import com.apithackathon.dropout.model.RiskScore;
import com.apithackathon.dropout.model.Student;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Risk Prediction Service - AI/ML logic for calculating student dropout risk
 * Implements the 7-factor risk assessment model
 */
@Service
public class RiskPredictionService {
    
    private static final double HIGH_RISK_THRESHOLD = 70.0;
    private static final double MODERATE_RISK_THRESHOLD = 50.0;
    
    /**
     * Calculate risk score for a student based on 7 factors
     */
    public RiskScore calculateRiskScore(Student student) {
        List<String> reasons = new ArrayList<>();
        double riskScore = 0.0;
        
        // Factor 1: Attendance Rate (weight: 30%)
        if (student.getAttendanceRate() < 60) {
            riskScore += 30;
            reasons.add(String.format("Low attendance (%.1f%%)", student.getAttendanceRate()));
        } else if (student.getAttendanceRate() < 75) {
            riskScore += 15;
            reasons.add(String.format("Moderate attendance (%.1f%%)", student.getAttendanceRate()));
        }
        
        // Factor 2: Exam Score (weight: 25%)
        if (student.getExamScore() < 45) {
            riskScore += 25;
            reasons.add(String.format("Below-average exam scores (%.1f)", student.getExamScore()));
        } else if (student.getExamScore() < 65) {
            riskScore += 12;
            reasons.add(String.format("Average exam scores (%.1f)", student.getExamScore()));
        }
        
        // Factor 3: Socio-Economic Status (weight: 15%)
        if ("Low".equalsIgnoreCase(student.getSocioEconomicStatus())) {
            riskScore += 15;
            reasons.add("Low socio-economic status");
        } else if ("Medium".equalsIgnoreCase(student.getSocioEconomicStatus())) {
            riskScore += 7;
        }
        
        // Factor 4: Transport Allowance (weight: 10%)
        if (!student.getTransportAllowanceUsed()) {
            riskScore += 10;
            reasons.add("Transport allowance not utilized");
        }
        
        // Factor 5: Migration Indicator (weight: 10%)
        if (student.getMigrationIndicator() == 1) {
            riskScore += 10;
            reasons.add("Seasonal migrant family");
        }
        
        // Factor 6: Gender (weight: 5%)
        if ("Female".equalsIgnoreCase(student.getGender())) {
            riskScore += 5;
            reasons.add("Female student (higher dropout risk)");
        }
        
        // Factor 7: Social Category (weight: 5%)
        if ("ST".equalsIgnoreCase(student.getSocialCategory()) || 
            "SC".equalsIgnoreCase(student.getSocialCategory())) {
            riskScore += 5;
            reasons.add("Belongs to ST/SC category");
        }
        
        // Determine risk level
        String riskLevel = determineRiskLevel(riskScore);
        
        // Create response
        RiskScore result = new RiskScore();
        result.setStudentId(student.getStudentId());
        result.setRiskLevel(riskLevel);
        result.setRiskProbability(riskScore);
        result.setRiskScore(riskScore);
        result.setReasons(reasons);
        result.setDistrict(student.getDistrict());
        
        // Add student details
        RiskScore.StudentDetails details = new RiskScore.StudentDetails(
            student.getAttendanceRate(),
            student.getExamScore(),
            student.getSocioEconomicStatus(),
            student.getTransportAllowanceUsed(),
            student.getMigrationIndicator(),
            student.getGender(),
            student.getSocialCategory()
        );
        result.setStudentDetails(details);
        
        return result;
    }
    
    /**
     * Determine risk level based on score
     */
    private String determineRiskLevel(double score) {
        if (score >= HIGH_RISK_THRESHOLD) {
            return "High";
        } else if (score >= MODERATE_RISK_THRESHOLD) {
            return "Moderate";
        } else {
            return "Low";
        }
    }
    
    /**
     * Check if student is high risk
     */
    public boolean isHighRisk(Student student) {
        RiskScore riskScore = calculateRiskScore(student);
        return "High".equals(riskScore.getRiskLevel());
    }
    
    /**
     * Get risk threshold values
     */
    public double getHighRiskThreshold() {
        return HIGH_RISK_THRESHOLD;
    }
    
    public double getModerateRiskThreshold() {
        return MODERATE_RISK_THRESHOLD;
    }
}
