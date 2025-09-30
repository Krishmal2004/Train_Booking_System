package com.example.train_systen.stationManager.dto;

import java.math.BigDecimal;

public class MonthlySalesDTO {
    private Integer month;
    private BigDecimal totalRevenue;

    // Corrected constructor to match the JPQL query's return types.
    // It now correctly accepts a BigDecimal for the totalRevenue.
    public MonthlySalesDTO(Integer month, BigDecimal totalRevenue) {
        this.month = month;
        this.totalRevenue = totalRevenue;
    }

    // Getters and Setters
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}