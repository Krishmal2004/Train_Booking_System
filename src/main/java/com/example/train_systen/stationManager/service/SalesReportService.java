package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.SalesReport;
import com.example.train_systen.stationManager.model.Ticket;
import com.example.train_systen.stationManager.repository.SalesReportRepository;
import com.example.train_systen.stationManager.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SalesReportService {

    private final SalesReportRepository salesReportRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public SalesReportService(SalesReportRepository salesReportRepository, TicketRepository ticketRepository) {
        this.salesReportRepository = salesReportRepository;
        this.ticketRepository = ticketRepository;
    }

    public SalesReport generateDailyReport(LocalDate date, String generatedBy) {
        LocalDate start = date;
        LocalDate end = date;
        return generateReport(start, end, "DAILY", generatedBy);
    }
    public SalesReport generateWeeklyReport(LocalDate startDate, LocalDate endDate, String generatedBy) {
        return generateReport(startDate, endDate, "WEEKLY", generatedBy);
    }
    public SalesReport generateMonthlyReport(LocalDate startDate, LocalDate endDate, String generatedBy) {
        return generateReport(startDate, endDate, "MONTHLY", generatedBy);
    }

    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Key Metrics
        summary.put("totalRevenue", ticketRepository.findTotalRevenue());
        summary.put("totalTicketsSold", ticketRepository.count());

        // Sales This Month
        LocalDate startOfMonth = YearMonth.now().atDay(1);
        LocalDate endOfMonth = YearMonth.now().atEndOfMonth();
        summary.put("monthlyRevenue", ticketRepository.findRevenueBetweenDates(startOfMonth, endOfMonth));

        // Sales This Year
        int currentYear = LocalDate.now().getYear();
        LocalDate startOfYear = LocalDate.of(currentYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(currentYear, 12, 31);
        summary.put("yearlyRevenue", ticketRepository.findRevenueBetweenDates(startOfYear, endOfYear));

        // Chart Data: Monthly sales for the current year
        summary.put("monthlySalesData", ticketRepository.findMonthlySalesForYear(currentYear));

        return summary;
    }

    private SalesReport generateReport(LocalDate startDate, LocalDate endDate, String reportType, String generatedBy) {
        // UPDATED: Fetch only tickets with the status "CONFIRMED"
        List<Ticket> tickets = ticketRepository.findByTravelDateBetweenAndStatus(startDate, endDate, "CONFIRMED");

        BigDecimal totalRevenue = tickets.stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int ticketsSold = tickets.size();

        BigDecimal averagePrice = ticketsSold > 0 ?
                totalRevenue.divide(new BigDecimal(ticketsSold), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        SalesReport report = salesReportRepository.findByReportDateAndReportType(startDate, reportType)
                .orElse(new SalesReport());

        report.setReportDate(startDate);
        report.setReportType(reportType);
        report.setTotalRevenue(totalRevenue);
        report.setTicketsSold(ticketsSold);
        report.setAverageTicketPrice(averagePrice);
        report.setGeneratedBy(generatedBy);

        return salesReportRepository.save(report);
    }

    public List<SalesReport> getAllReports() {
        return salesReportRepository.findAll();
    }

    public Optional<SalesReport> getReportById(Long id) {
        return salesReportRepository.findById(id);
    }

    public SalesReport saveReport(SalesReport salesReport) {
        return salesReportRepository.save(salesReport);
    }

    public void deleteReport(Long id) {
        salesReportRepository.deleteById(id);
    }
}