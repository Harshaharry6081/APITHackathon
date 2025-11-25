package com.apithackathon.dropout.repository;

import com.apithackathon.dropout.model.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Intervention Repository - Data access layer for interventions
 */
@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {
    
    /**
     * Find interventions by student ID
     */
    List<Intervention> findByStudentId(String studentId);
    
    /**
     * Find interventions by student ID, ordered by date descending
     */
    List<Intervention> findByStudentIdOrderByInterventionDateDesc(String studentId);
    
    /**
     * Find interventions by district
     */
    List<Intervention> findByDistrict(String district);
    
    /**
     * Find interventions by status
     */
    List<Intervention> findByStatus(String status);
    
    /**
     * Find recent interventions
     */
    @Query("SELECT i FROM Intervention i ORDER BY i.interventionDate DESC")
    List<Intervention> findRecentInterventions();
    
    /**
     * Find interventions within date range
     */
    @Query("SELECT i FROM Intervention i WHERE i.interventionDate BETWEEN :startDate AND :endDate")
    List<Intervention> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count interventions by district
     */
    Long countByDistrict(String district);
    
    /**
     * Count interventions by status
     */
    Long countByStatus(String status);
}
