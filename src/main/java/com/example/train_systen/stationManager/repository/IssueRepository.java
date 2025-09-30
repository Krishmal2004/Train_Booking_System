package com.example.train_systen.stationManager.repository;

import com.example.train_systen.stationManager.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    // Find by ticket ID
    Issue findByTicketId(String ticketId);

    // Filter by status
    List<Issue> findByStatus(String status);

    // Filter by priority
    List<Issue> findByPriority(String priority);

    // Filter by system affected
    List<Issue> findBySystemAffected(String systemAffected);

    // Filter by multiple criteria
    List<Issue> findByStatusAndPriorityAndSystemAffected(String status, String priority, String systemAffected);

    // Filter by status and priority
    List<Issue> findByStatusAndPriority(String status, String priority);

    // Filter by status and system
    List<Issue> findByStatusAndSystemAffected(String status, String systemAffected);

    // Filter by priority and system
    List<Issue> findByPriorityAndSystemAffected(String priority, String systemAffected);

    // Search by keyword in title, description, or ticket ID
    @Query("SELECT i FROM Issue i WHERE " +
            "LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.ticketId) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Issue> searchByKeyword(@Param("keyword") String keyword);

    // Count issues by status
    long countByStatus(String status);

    // Count issues by priority
    long countByPriority(String priority);

    // Find issues assigned to a specific user
    List<Issue> findByAssigneeName(String assigneeName);

    // Find recent issues, ordered by creation date
    List<Issue> findTop5ByOrderByCreatedAtDesc();
}