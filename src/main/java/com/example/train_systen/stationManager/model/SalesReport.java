package com.example.train_systen.stationManager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_reports")
public class SalesReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate reportDate;

    @Column(nullable = false)
    private String reportType; // e.g., "DAILY", "WEEKLY", "MONTHLY"

    @Column(nullable = false)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private int ticketsSold;

    private BigDecimal averageTicketPrice;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    private String generatedBy;

    @Lob
    private String notes;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }

    // --- GETTERS AND SETTERS ---
    // The missing methods are below

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public BigDecimal getAverageTicketPrice() {
        return averageTicketPrice;
    }

    public void setAverageTicketPrice(BigDecimal averageTicketPrice) {
        this.averageTicketPrice = averageTicketPrice;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}