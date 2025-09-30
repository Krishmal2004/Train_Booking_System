package com.example.train_systen.stationManager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "support_issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ticketId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Priority is required")
    private String priority;

    @NotNull(message = "System affected is required")
    private String systemAffected;

    @NotNull(message = "Status is required")
    private String status;

    @NotBlank(message = "Reporter name is required")
    private String reporterName;

    private String assigneeName;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime resolvedAt;

    // Default constructor
    public Issue() {
    }

    // Constructor with all fields
    public Issue(Long id, String ticketId, String title, String description, String priority,
                 String systemAffected, String status, String reporterName, String assigneeName,
                 String resolution, LocalDateTime createdAt, LocalDateTime updatedAt,
                 LocalDateTime resolvedAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.systemAffected = systemAffected;
        this.status = status;
        this.reporterName = reporterName;
        this.assigneeName = assigneeName;
        this.resolution = resolution;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSystemAffected() {
        return systemAffected;
    }

    public void setSystemAffected(String systemAffected) {
        this.systemAffected = systemAffected;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return Objects.equals(id, issue.id) &&
                Objects.equals(ticketId, issue.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ticketId);
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", ticketId='" + ticketId + '\'' +
                ", title='" + title + '\'' +
                ", priority='" + priority + '\'' +
                ", systemAffected='" + systemAffected + '\'' +
                ", status='" + status + '\'' +
                ", reporterName='" + reporterName + '\'' +
                ", assigneeName='" + assigneeName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    // Method to generate ticket ID
    public void generateTicketId() {
        if (this.ticketId == null || this.ticketId.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            String year = String.valueOf(now.getYear());
            String month = String.format("%02d", now.getMonthValue());
            String day = String.format("%02d", now.getDayOfMonth());

            // Get the count of tickets today (this would typically come from a service)
            // For simplicity, we'll use a timestamp-based approach
            String count = String.format("%04d", now.getHour() * 100 + now.getMinute());

            this.ticketId = "IT-" + year + month + day + "-" + count;
        }
    }

    // Lifecycle methods
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        updateResolvedTimestamp();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        updateResolvedTimestamp();
    }

    // Update resolved timestamp if status changes to Resolved
    private void updateResolvedTimestamp() {
        if ("Resolved".equals(this.status) && this.resolvedAt == null) {
            this.resolvedAt = LocalDateTime.now();
        } else if (!"Resolved".equals(this.status) && !"Closed".equals(this.status)) {
            this.resolvedAt = null;
        }
    }
}