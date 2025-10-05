package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.model.*;
import com.example.train_systen.stationManager.model.Package;
import com.example.train_systen.stationManager.service.PackageService;
import com.example.train_systen.stationManager.service.RouteService;
import com.example.train_systen.stationManager.service.ScheduleService;
import com.example.train_systen.stationManager.service.TicketService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final TicketService ticketService;
    private final RouteService routeService;
    private final PackageService packageService;
    private final ScheduleService scheduleService; // <-- ADD THIS

    @Autowired
    public DashboardController(TicketService ticketService, RouteService routeService, PackageService packageService, ScheduleService scheduleService) { // <-- UPDATE CONSTRUCTOR
        this.ticketService = ticketService;
        this.routeService = routeService;
        this.packageService = packageService;
        this.scheduleService = scheduleService; // <-- ADD THIS
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // Session Check
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // Get username from the session
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login?error=session_expired";
        }

        // Fetch user's tickets
        List<Ticket> userTickets = ticketService.getTicketsByPassengerName(username);
        model.addAttribute("tickets", userTickets);

        // Fetch all routes
        List<Route> allRoutes = routeService.getAllRoutes();
        model.addAttribute("routes", allRoutes);

        // Fetch all seasonal packages
        List<Package> allPackages = packageService.getAllPackages();
        model.addAttribute("packages", allPackages);


        // Fetch all train schedules
        List<Schedule> allSchedules = scheduleService.getAllSchedules();
        model.addAttribute("schedules", allSchedules);
        // --- END NEW ---

        model.addAttribute("issue", new Issue());

        model.addAttribute("currentUser", username);
        return "dashboard";
    }
}