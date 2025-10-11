package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.model.Issue;
import com.example.train_systen.stationManager.service.IssueService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/issues") // Base path for all issue-related endpoints
public class IssueController {

    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        model.addAllAttributes(issueService.getDashboardSummary());
        // Assuming your HTML views are in 'resources/templates/stationManager/issues/'
        return "itSupportSystem/reports/dashboard";
    }

    @GetMapping
    public String listIssues(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        model.addAttribute("issues", issueService.getAllIssues());
        return "itSupportSystem/reports/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        model.addAttribute("issue", new Issue());
        return "itSupportSystem/reports/create";
    }

    @PostMapping("/create")
    public String createIssue(@ModelAttribute Issue issue, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        // Set reporter name from session if available, otherwise it's from the form
        String username = (String) session.getAttribute("username");
        if (username != null && (issue.getReporterName() == null || issue.getReporterName().isEmpty())) {
            issue.setReporterName(username);
        }

        issueService.saveIssue(issue);
        redirectAttributes.addFlashAttribute("successMessage", "Issue reported successfully!");
        return "redirect:/issues";
    }

    @GetMapping("/{id}/update")
    public String showUpdateForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        Issue issue = issueService.getIssueById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid issue ID: " + id));
        model.addAttribute("issue", issue);
        return "itSupportSystem/reports/update";
    }

    @PostMapping("/{id}/update")
    public String updateIssue(@PathVariable Long id, @ModelAttribute Issue issueDetails, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        Issue existingIssue = issueService.getIssueById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid issue ID: " + id));

        // Update only the editable fields
        existingIssue.setTitle(issueDetails.getTitle());
        existingIssue.setDescription(issueDetails.getDescription());
        existingIssue.setStatus(issueDetails.getStatus());
        existingIssue.setPriority(issueDetails.getPriority());

        issueService.saveIssue(existingIssue);
        redirectAttributes.addFlashAttribute("successMessage", "Issue #" + id + " updated successfully.");
        return "redirect:/issues";
    }

    @GetMapping("/{id}/delete")
    public String deleteIssue(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        issueService.deleteIssue(id);
        redirectAttributes.addFlashAttribute("successMessage", "Issue #" + id + " has been deleted.");
        return "redirect:/issues";
    }

    @GetMapping("/{id}/resolve")
    public String showResolveForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        Issue issue = issueService.getIssueById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid issue ID: " + id));
        model.addAttribute("issue", issue);
        return "itSupportSystem/reports/resolve";
    }

    @PostMapping("/{id}/resolve")
    public String resolveIssue(@PathVariable Long id,
                               @RequestParam("resolution") String resolution,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        issueService.resolveIssue(id, resolution);
        redirectAttributes.addFlashAttribute("successMessage", "Issue #" + id + " has been resolved.");
        return "redirect:/issues";
    }
}