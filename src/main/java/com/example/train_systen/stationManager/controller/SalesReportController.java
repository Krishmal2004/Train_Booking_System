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
    public String generateReport(@RequestParam("reportDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate,
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        String username = (String) session.getAttribute("username");
        salesReportService.generateDailyReport(reportDate, username);
        redirectAttributes.addFlashAttribute("successMessage", "Daily sales report for " + reportDate + " generated successfully.");
        // Change this line to redirect to the dashboard
        return "redirect:/sales-reports";
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
    public String updateReport(@PathVariable Long id, @ModelAttribute SalesReport report,
                               RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        report.setId(id); // Ensure the ID is set for the update
        salesReportService.saveReport(report);
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