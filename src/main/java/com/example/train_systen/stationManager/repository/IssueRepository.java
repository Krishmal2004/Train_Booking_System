package com.example.train_systen.stationManager.repository;

import com.example.train_systen.stationManager.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    // Custom query methods for dashboard statistics
    long countByStatus(String status);

    long countByPriority(String priority);

    // Counts issues resolved between a start and end time
    long countByStatusAndUpdatedAtBetween(String status, LocalDateTime start, LocalDateTime end);
}