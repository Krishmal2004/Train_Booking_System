package com.example.train_systen.stationManager.repository;

import com.example.train_systen.stationManager.model.SalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface SalesReportRepository extends JpaRepository<SalesReport, Long> {
    Optional<SalesReport> findByReportDateAndReportType(LocalDate reportDate, String reportType);

}