package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Issue;
import com.example.train_systen.stationManager.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;

    @Autowired
    public IssueServiceImpl(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    @Override
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    @Override
    public Optional<Issue> getIssueById(Long id) {
        return issueRepository.findById(id);
    }

    @Override
    public Issue saveIssue(Issue issue) {
        return issueRepository.save(issue);
    }

    @Override
    public void deleteIssue(Long id) {
        issueRepository.deleteById(id);
    }

    @Override
    public void resolveIssue(Long issueId, String resolution) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid issue ID: " + issueId));

        issue.setStatus("Resolved");
        issue.setResolution(resolution);
        issue.setResolvedAt(LocalDateTime.now());

        issueRepository.save(issue);
    }


    @Override
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Key Metrics
        summary.put("openIssuesCount", issueRepository.countByStatus("Open"));
        summary.put("inProgressIssuesCount", issueRepository.countByStatus("In Progress"));
        summary.put("highPriorityIssuesCount", issueRepository.countByPriority("High"));

        long resolvedToday = issueRepository.countByStatusAndUpdatedAtBetween(
                "Resolved",
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX)
        );
        summary.put("resolvedTodayCount", resolvedToday);

        // Data for Status Chart
        Map<String, Long> statusCounts = new HashMap<>();
        statusCounts.put("open", issueRepository.countByStatus("Open"));
        statusCounts.put("inProgress", issueRepository.countByStatus("In Progress"));
        statusCounts.put("resolved", issueRepository.countByStatus("Resolved"));
        summary.put("statusCounts", statusCounts);

        // Data for Priority Chart
        Map<String, Long> priorityCounts = new HashMap<>();
        priorityCounts.put("low", issueRepository.countByPriority("Low"));
        priorityCounts.put("medium", issueRepository.countByPriority("Medium"));
        priorityCounts.put("high", issueRepository.countByPriority("High"));
        summary.put("priorityCounts", priorityCounts);

        return summary;
    }
}