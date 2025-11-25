package com.apithackathon.dropout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Model Metrics DTO - ML model performance metrics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelMetrics {
    private Double accuracy;
    private Double inclusionError;
    private Double exclusionError;
    private Boolean pocCriteriaMet;
    private Integer totalStudents;
    private Map<String, Integer> riskDistribution;
}
