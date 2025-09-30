package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Issue;
import com.example.train_systen.stationManager.repository.IssueRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IssueService {

    private final IssueRepository issueRepository;

    // Constructor injection
    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    /**
     * Create a new issue
     */
    @Transactional
    public Issue createIssue(Issue issue) {
        // Generate ticket ID if not already set
        issue.generateTicketId();

        // Set initial status if not provided
        if (issue.getStatus() == null || issue.getStatus().isEmpty()) {
            issue.setStatus("Open");
        }

        return issueRepository.save(issue);
    }

    /**
     * Get all issues
     */
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    /**
     * Get issue by ID
     */
    public Issue getIssueById(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with ID: " + id));
    }

    /**
     * Update an existing issue
     */
    @Transactional
    public Issue updateIssue(Long id, Issue issueDetails) {
        Issue issue = getIssueById(id);

        // Update fields
        issue.setTitle(issueDetails.getTitle());
        issue.setDescription(issueDetails.getDescription());
        issue.setPriority(issueDetails.getPriority());
        issue.setSystemAffected(issueDetails.getSystemAffected());
        issue.setAssigneeName(issueDetails.getAssigneeName());

        // Update status and set resolvedAt if status is now Resolved
        String oldStatus = issue.getStatus();
        String newStatus = issueDetails.getStatus();
        issue.setStatus(newStatus);

        // Set resolution text if provided
        if (issueDetails.getResolution() != null) {
            issue.setResolution(issueDetails.getResolution());
        }

        return issueRepository.save(issue);
    }

    /**
     * Update issue status
     */
    @Transactional
    public Issue updateIssueStatus(Long id, String newStatus) {
        Issue issue = getIssueById(id);
        issue.setStatus(newStatus);

        return issueRepository.save(issue);
    }

    /**
     * Delete/remove an issue
     */
    @Transactional
    public void deleteIssue(Long id) {
        Issue issue = getIssueById(id);
        issueRepository.delete(issue);
    }

    /**
     * Filter issues by criteria
     */
    public List<Issue> filterIssues(String status, String priority, String system) {
        // All filters are provided
        if (status != null && !status.isEmpty() &&
                priority != null && !priority.isEmpty() &&
                system != null && !system.isEmpty()) {
            return issueRepository.findByStatusAndPriorityAndSystemAffected(status, priority, system);
        }

        // Only status and priority
        if (status != null && !status.isEmpty() &&
                priority != null && !priority.isEmpty()) {
            return issueRepository.findByStatusAndPriority(status, priority);
        }

        // Only status and system
        if (status != null && !status.isEmpty() &&
                system != null && !system.isEmpty()) {
            return issueRepository.findByStatusAndSystemAffected(status, system);
        }

        // Only priority and system
        if (priority != null && !priority.isEmpty() &&
                system != null && !system.isEmpty()) {
            return issueRepository.findByPriorityAndSystemAffected(priority, system);
        }

        // Only status
        if (status != null && !status.isEmpty()) {
            return issueRepository.findByStatus(status);
        }

        // Only priority
        if (priority != null && !priority.isEmpty()) {
            return issueRepository.findByPriority(priority);
        }

        // Only system
        if (system != null && !system.isEmpty()) {
            return issueRepository.findBySystemAffected(system);
        }

        // No filters, return all
        return issueRepository.findAll();
    }

    /**
     * Search issues by keyword
     */
    public List<Issue> searchIssues(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return issueRepository.findAll();
        }
        return issueRepository.searchByKeyword(keyword);
    }

    /**
     * Get dashboard statistics
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Issue counts by status
        stats.put("openTickets", issueRepository.countByStatus("Open"));
        stats.put("inProgressTickets", issueRepository.countByStatus("In Progress"));
        stats.put("resolvedTickets", issueRepository.countByStatus("Resolved"));
        stats.put("closedTickets", issueRepository.countByStatus("Closed"));

        // Issue counts by priority
        stats.put("highPriorityTickets", issueRepository.countByPriority("High"));
        stats.put("mediumPriorityTickets", issueRepository.countByPriority("Medium"));
        stats.put("lowPriorityTickets", issueRepository.countByPriority("Low"));

        // Recent issues
        stats.put("allTickets", issueRepository.findTop5ByOrderByCreatedAtDesc());

        return stats;
    }

    /**
     * Get recent issues for dashboard
     */
    public List<Issue> getRecentIssues() {
        return issueRepository.findTop5ByOrderByCreatedAtDesc();
    }
}