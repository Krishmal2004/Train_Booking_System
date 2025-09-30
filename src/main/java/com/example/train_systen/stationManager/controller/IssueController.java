package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.model.Issue;
import com.example.train_systen.stationManager.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reports")
public class IssueController {

    private final IssueService issueService;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor injection
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    /**
     * List all issues with optional filtering
     */
    @GetMapping
    public String listIssues(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String system,
            Model model) {

        List<Issue> issues = issueService.filterIssues(status, priority, system);
        model.addAttribute("issues", issues);
        model.addAttribute("currentUser", "IT24103866");
        model.addAttribute("currentDateTime", LocalDateTime.now().format(DATETIME_FORMATTER));

        // Add GitHub repositories as list for list.html
        model.addAttribute("githubRepos", new String[]{
                "IT24103866/Train",
                "IT2120-PS/lab-sheet-submissions-IT24103866",
                "IT2140-DDD/lab-sheet-submissions-IT24103866",
                "IT2011-AI-ML/lab-sheet-submissions-IT24103866",
                "SE2030-Y2S1/se2030-lab-IT24103866"
        });

        return "itSupportSystem/reports/list"; // Just use the template name without the path
    }

    /**
     * Search for issues
     */
    @GetMapping("/search")
    public String searchIssues(@RequestParam String keyword, Model model) {
        List<Issue> issues = issueService.searchIssues(keyword);
        model.addAttribute("issues", issues);
        model.addAttribute("currentUser", "IT24103866");
        model.addAttribute("currentDateTime", LocalDateTime.now().format(DATETIME_FORMATTER));

        // Add GitHub repositories
        model.addAttribute("githubRepos", new String[]{
                "IT24103866/Train",
                "IT2120-PS/lab-sheet-submissions-IT24103866",
                "IT2140-DDD/lab-sheet-submissions-IT24103866",
                "IT2011-AI-ML/lab-sheet-submissions-IT24103866",
                "SE2030-Y2S1/se2030-lab-IT24103866"
        });

        return "itSupportSystem/reports/list";
    }

    /**
     * Show issue creation form
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("issue", new Issue());
        model.addAttribute("currentUser", "IT24103866");
        model.addAttribute("currentDateTime", LocalDateTime.now().format(DATETIME_FORMATTER));
        return "itSupportSystem/reports/create";
    }

    /**
     * Process issue creation
     */
    @PostMapping("/create")
    public String createIssue(@Valid @ModelAttribute("issue") Issue issue,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("currentUser", "IT24103866");
            model.addAttribute("currentDateTime", LocalDateTime.now().format(DATETIME_FORMATTER));
            return "itSupportSystem/reports/create";
        }

        issueService.createIssue(issue);
        redirectAttributes.addFlashAttribute("successMessage", "Issue report created successfully");

        return "redirect:/reports/dashboard";
    }

    /**
     * View issue details
     */
    @GetMapping("/{id}")
    public String viewIssue(@PathVariable Long id, Model model) {
        Issue issue = issueService.getIssueById(id);
        model.addAttribute("issue", issue);
        model.addAttribute("currentUser", "IT24103866");
        model.addAttribute("currentDateTime", LocalDateTime.now().format(DATETIME_FORMATTER));

        return "itSupportSystem/reports/view";
    }

    /**
     * Show issue edit form
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Issue issue = issueService.getIssueById(id);
        model.addAttribute("issue", issue);
        model.addAttribute("currentUser", "IT24103866");
        model.addAttribute("currentDateTime", LocalDateTime.now().format(DATETIME_FORMATTER));

        // Add GitHub repositories
        model.addAttribute("githubRepos", new String[]{
                "IT24103866/Train",
                "IT2120-PS/lab-sheet-submissions-IT24103866",
                "IT2140-DDD/lab-sheet-submissions-IT24103866",
                "IT2011-AI-ML/lab-sheet-submissions-IT24103866",
                "SE2030-Y2S1/se2030-lab-IT24103866"
        });

        return "itSupportSystem/reports/edit";
    }

    /**
     * Process issue update
     */
    @PostMapping("/{id}/edit")
    public String updateIssue(@PathVariable Long id,
                              @Valid @ModelAttribute("issue") Issue issue,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("currentUser", "IT24103866");
            model.addAttribute("currentDateTime", LocalDateTime.now().format(DATETIME_FORMATTER));

            // Add GitHub repositories
            model.addAttribute("githubRepos", new String[]{
                    "IT24103866/Train",
                    "IT2120-PS/lab-sheet-submissions-IT24103866",
                    "IT2140-DDD/lab-sheet-submissions-IT24103866",
                    "IT2011-AI-ML/lab-sheet-submissions-IT24103866",
                    "SE2030-Y2S1/se2030-lab-IT24103866"
            });

            return "itSupportSystem/reports/edit";
        }

        issueService.updateIssue(id, issue);
        redirectAttributes.addFlashAttribute("successMessage", "Issue report updated successfully");

        return "redirect:/itSupportSystem/reports";
    }

    /**
     * Quick status update
     */
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String newStatus,
                               RedirectAttributes redirectAttributes) {

        issueService.updateIssueStatus(id, newStatus);
        redirectAttributes.addFlashAttribute("successMessage", "Issue status updated successfully");

        return "redirect:/itSupportSystem/reports/dashboard";
    }

    /**
     * Show delete confirmation
     */
    @GetMapping("/{id}/delete")
    public String showDeleteConfirmation(@PathVariable Long id, Model model) {
        Issue issue = issueService.getIssueById(id);
        model.addAttribute("issue", issue);
        model.addAttribute("currentUser", "IT24103866");
        model.addAttribute("currentDateTime", LocalDateTime.now().format(DATETIME_FORMATTER));

        return "itSupportSystem/reports/delete-confirmation";
    }

    /**
     * Process issue deletion
     */
    @PostMapping("/{id}/delete-confirm")
    public String deleteIssue(@PathVariable Long id,
                              @RequestParam String removalReason,
                              @RequestParam(required = false) String removalNotes,
                              RedirectAttributes redirectAttributes) {

        issueService.deleteIssue(id);
        redirectAttributes.addFlashAttribute("successMessage", "Issue report removed successfully");

        return "redirect:/itSupportSystem/reports";
    }

    /**
     * Dashboard view
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("currentUser", "IT24103866");
        model.addAttribute("currentDateTime", LocalDateTime.now().format(DATETIME_FORMATTER));

        // Get dashboard stats
        var stats = issueService.getDashboardStats();
        model.addAllAttributes(stats);

        // Add GitHub repositories as objects with url and name for dashboard.html
        List<Map<String, String>> repos = new ArrayList<>();
        repos.add(createRepo("IT24103866/Train", "https://github.com/IT24103866/Train"));
        repos.add(createRepo("IT2120-PS/lab-sheet-submissions-IT24103866", "https://github.com/IT2120-PS/lab-sheet-submissions-IT24103866"));
        repos.add(createRepo("IT2140-DDD/lab-sheet-submissions-IT24103866", "https://github.com/IT2140-DDD/lab-sheet-submissions-IT24103866"));
        repos.add(createRepo("IT2011-AI-ML/lab-sheet-submissions-IT24103866", "https://github.com/IT2011-AI-ML/lab-sheet-submissions-IT24103866"));
        repos.add(createRepo("SE2030-Y2S1/se2030-lab-IT24103866", "https://github.com/SE2030-Y2S1/se2030-lab-IT24103866"));

        model.addAttribute("githubRepos", repos);

        // Change this to match where your dashboard.html actually is
        return "/itSupportSystem/reports/dashboard"; // If it's at src/main/resources/templates/dashboard.html
    }

    private Map<String, String> createRepo(String name, String url) {
        Map<String, String> repo = new HashMap<>();
        repo.put("name", name);
        repo.put("url", url);
        return repo;
    }

    /**
     * Export issues to CSV
     */
    @GetMapping("/export/csv")
    public String exportToCsv() {
        // Implementation for CSV export would go here
        return "redirect:/itSupportSystem/reports/dashboard";
    }

    /**
     * Export issues to PDF
     */
    @GetMapping("/export/pdf")
    public String exportToPdf() {
        // Implementation for PDF export would go here
        return "redirect:/itSupportSystem/reports/dashboard";
    }
}