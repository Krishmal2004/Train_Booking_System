package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.model.SalesReport;
import com.example.train_systen.stationManager.service.SalesReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/sales-reports")
public class SalesReportController {

    private final SalesReportService salesReportService;

    @Autowired
    public SalesReportController(SalesReportService salesReportService) {
        this.salesReportService = salesReportService;
    }
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        model.addAllAttributes(salesReportService.getDashboardSummary());
        model.addAttribute("currentUser", session.getAttribute("username"));
        return "ticketOfficer/sales/dashboard";
    }

    @GetMapping
    public String listReports(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        model.addAttribute("reports", salesReportService.getAllReports());
        model.addAttribute("currentUser", session.getAttribute("username"));
        return "ticketOfficer/sales/list";
    }

    @GetMapping("/generate")
    public String showGenerateForm(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        model.addAttribute("today", LocalDate.now());
        return "ticketOfficer/sales/generate";
    }

    @PostMapping("/generate")
    public String generateReport(@RequestParam("reportType") String reportType,
                                 @RequestParam(value = "reportDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate,
                                 @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 HttpSession session, RedirectAttributes redirectAttributes) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        String username = (String) session.getAttribute("username");
        String successMessage = "";

        // Use a switch to handle different report types
        switch (reportType) {
            case "daily":
                if (reportDate == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Please select a date for the daily report.");
                    return "redirect:/sales-reports/generate";
                }
                salesReportService.generateDailyReport(reportDate, username);
                successMessage = "Daily sales report for " + reportDate + " generated successfully.";
                break;

            case "weekly":
                if (startDate == null || endDate == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Please select a start and end date for the weekly report.");
                    return "redirect:/sales-reports/generate";
                }
                salesReportService.generateWeeklyReport(startDate, endDate, username);
                successMessage = "Weekly sales report from " + startDate + " to " + endDate + " generated successfully.";
                break;

            case "monthly":
                if (startDate == null || endDate == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Please select a start and end date for the monthly report.");
                    return "redirect:/sales-reports/generate";
                }
                salesReportService.generateMonthlyReport(startDate, endDate, username);
                successMessage = "Monthly sales report from " + startDate + " to " + endDate + " generated successfully.";
                break;

            default:
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid report type selected.");
                return "redirect:/sales-reports/generate";
        }

        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/sales-reports"; // Redirect to the list of reports
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        SalesReport report = salesReportService.getReportById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report ID:" + id));
        model.addAttribute("report", report);
        return "ticketOfficer/sales/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateReport(@PathVariable Long id,
                               @ModelAttribute("report") SalesReport reportDetails, // Contains data from the form
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // 1. Fetch the existing report from the database
        SalesReport existingReport = salesReportService.getReportById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report ID:" + id));

        // 2. Update only the fields you want to change
        existingReport.setNotes(reportDetails.getNotes());

        // 3. Save the updated existing report
        salesReportService.saveReport(existingReport);

        redirectAttributes.addFlashAttribute("successMessage", "Report updated successfully.");
        return "redirect:/sales-reports";
    }

    @GetMapping("/{id}/delete")
    public String deleteReport(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        salesReportService.deleteReport(id);
        redirectAttributes.addFlashAttribute("successMessage", "Report deleted successfully.");
        return "redirect:/sales-reports";
    }
}