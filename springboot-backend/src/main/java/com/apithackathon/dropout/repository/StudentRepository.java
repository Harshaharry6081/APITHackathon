package com.apithackathon.dropout.repository;

import com.apithackathon.dropout.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Student Repository - Data access layer for students
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    /**
     * Find student by student ID
     */
    Optional<Student> findByStudentId(String studentId);
    
    /**
     * Find all students in a district
     */
    List<Student> findByDistrict(String district);
    
    /**
     * Find students by risk level
     */
    List<Student> findByDropoutRisk(String dropoutRisk);
    
    /**
     * Find high-risk students (custom query)
     */
    @Query("SELECT s FROM Student s WHERE s.dropoutRisk = 'High' OR s.riskProbability >= :threshold")
    List<Student> findHighRiskStudents(@Param("threshold") Double threshold);
    
    /**
     * Find students by district and risk level
     */
    List<Student> findByDistrictAndDropoutRisk(String district, String dropoutRisk);
    
    /**
     * Count students by district
     */
    Long countByDistrict(String district);
    
    /**
     * Count students by risk level
     */
    Long countByDropoutRisk(String dropoutRisk);
    
    /**
     * Find students with risk probability above threshold
     */
    @Query("SELECT s FROM Student s WHERE s.riskProbability >= :threshold ORDER BY s.riskProbability DESC")
    List<Student> findByRiskProbabilityGreaterThanEqualOrderByRiskProbabilityDesc(@Param("threshold") Double threshold);
    
    /**
     * Get average risk score by district
     */
    @Query("SELECT AVG(s.riskScore) FROM Student s WHERE s.district = :district")
    Double getAverageRiskScoreByDistrict(@Param("district") String district);
    
    /**
     * Get all districts
     */
    @Query("SELECT DISTINCT s.district FROM Student s")
    List<String> findAllDistricts();
}
