package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Issue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IssueService {

    List<Issue> getAllIssues();

    Optional<Issue> getIssueById(Long id);

    Issue saveIssue(Issue issue);

    void deleteIssue(Long id);

    Map<String, Object> getDashboardSummary();

    void resolveIssue(Long issueId, String resolution);
}