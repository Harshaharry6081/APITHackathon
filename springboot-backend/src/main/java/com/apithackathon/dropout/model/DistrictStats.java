package com.apithackathon.dropout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * District Statistics DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictStats {
    private String district;
    private Integer totalStudents;
    private Integer highRiskCount;
    private Integer moderateRiskCount;
    private Integer lowRiskCount;
    private Double averageRiskScore;
    private Integer interventionsActive;
}
