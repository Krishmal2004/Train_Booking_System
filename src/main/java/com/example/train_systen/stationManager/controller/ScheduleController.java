package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.model.Schedule;
import com.example.train_systen.stationManager.service.RouteService;
import com.example.train_systen.stationManager.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final RouteService routeService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService, RouteService routeService) {
        this.scheduleService = scheduleService;
        this.routeService = routeService;
    }

    @GetMapping
    public String listSchedules(Model model,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                @RequestParam(required = false) Long routeId) {

        List<Schedule> schedules;

        if (status != null && !status.isEmpty()) {
            schedules = scheduleService.getSchedulesByStatus(status);
        } else if (date != null) {
            schedules = scheduleService.getSchedulesByDate(date);
        } else if (routeId != null) {
            schedules = scheduleService.getSchedulesByRouteId(routeId);
        } else {
            schedules = scheduleService.getAllSchedules();
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("today", LocalDate.now());
        return "stationManager/schedule/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("schedule", new Schedule());
        model.addAttribute("routes", routeService.getAllRoutes());
        return "stationManager/schedule/create";
    }

    @PostMapping("/create")
    public String createSchedule(@ModelAttribute("schedule") Schedule schedule, // Removed @Valid for now
                                 BindingResult result,
                                 @RequestParam Long routeId,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        // 1. SET THE ROUTE FIRST - This is the most important change.
        routeService.getRouteById(routeId).ifPresent(schedule::setRoute);

        // 2. NOW, MANUALLY VALIDATE THE SCHEDULE
        // Check if the route was actually set (it's required)
        if (schedule.getRoute() == null) {
            // "route" here must match the field name in the Schedule class
            result.rejectValue("route", "error.schedule", "A valid route is required.");
        }

        // Check for other required fields manually if needed (example for scheduleId)
        if (schedule.getScheduleId() == null || schedule.getScheduleId().isBlank()) {
            result.rejectValue("scheduleId", "error.schedule", "Schedule ID is required.");
        } else if (scheduleService.isScheduleIdExists(schedule.getScheduleId())) {
            result.rejectValue("scheduleId", "error.schedule", "Schedule ID already exists.");
        }

        // Validate times
        if (schedule.getDepartureTime() != null && schedule.getArrivalTime() != null && schedule.getDepartureTime().isAfter(schedule.getArrivalTime())) {
            result.rejectValue("arrivalTime", "error.schedule", "Arrival time must be after departure time.");
        }

        // 3. CHECK FOR ANY ERRORS
        if (result.hasErrors()) {
            model.addAttribute("routes", routeService.getAllRoutes());
            return "stationManager/schedule/create"; // Return to the form if there are errors
        }

        // 4. SAVE IF EVERYTHING IS OK
        scheduleService.saveSchedule(schedule);
        redirectAttributes.addFlashAttribute("successMessage", "Schedule created successfully!");
        return "redirect:/schedules";
    }

    @GetMapping("/{id}")
    public String viewSchedule(@PathVariable Long id, Model model) {
        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid schedule ID: " + id));
        model.addAttribute("schedule", schedule);
        return "stationManager/schedule/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Schedule schedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid schedule ID: " + id));

        model.addAttribute("schedule", schedule);
        model.addAttribute("routes", routeService.getAllRoutes());
        return "stationManager/schedule/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateSchedule(@PathVariable Long id,
                                 @ModelAttribute("schedule") Schedule schedule, // Removed @Valid
                                 BindingResult result,
                                 @RequestParam Long routeId,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        // 1. SET THE ROUTE FIRST
        routeService.getRouteById(routeId).ifPresent(schedule::setRoute);

        // 2. MANUALLY VALIDATE
        // Check if the route was set
        if (schedule.getRoute() == null) {
            result.rejectValue("route", "error.schedule", "A valid route is required.");
        }

        // Check if scheduleId is being changed and if the new one already exists
        Schedule existingSchedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid schedule ID: " + id));

        if (!existingSchedule.getScheduleId().equals(schedule.getScheduleId()) &&
                scheduleService.isScheduleIdExists(schedule.getScheduleId())) {
            result.rejectValue("scheduleId", "error.schedule", "Schedule ID already exists.");
        }

        // Validate times
        if (schedule.getDepartureTime() != null && schedule.getArrivalTime() != null && schedule.getDepartureTime().isAfter(schedule.getArrivalTime())) {
            result.rejectValue("arrivalTime", "error.schedule", "Arrival time must be after departure time.");
        }

        // 3. CHECK FOR ERRORS
        if (result.hasErrors()) {
            model.addAttribute("routes", routeService.getAllRoutes());
            return "stationManager/schedule/edit"; // Return to form if errors exist
        }

        // 4. SAVE IF EVERYTHING IS OK
        schedule.setId(id); // Ensure the original ID is preserved
        scheduleService.saveSchedule(schedule);
        redirectAttributes.addFlashAttribute("successMessage", "Schedule updated successfully!");
        return "redirect:/schedules";
    }

    @PostMapping("/{id}/delete")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        scheduleService.deleteSchedule(id);
        redirectAttributes.addFlashAttribute("successMessage", "Schedule deleted successfully");
        return "redirect:/schedules";
    }

    @GetMapping("/report")
    public String showReportForm(Model model) {
        model.addAttribute("routes", routeService.getAllRoutes());
        return "stationManager/schedule/report";
    }

    @GetMapping("/generate-report")
    public String generateReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 @RequestParam(required = false) Long routeId,
                                 Model model) {

        List<Schedule> schedules = scheduleService.getSchedulesByDateRange(startDate, endDate);

        if (routeId != null) {
            // Filter by route if specified
            schedules = schedules.stream()
                    .filter(s -> s.getRoute() != null && s.getRoute().getId().equals(routeId))
                    .toList();
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("routeId", routeId);

        // Get the route details if routeId is provided
        if (routeId != null) {
            routeService.getRouteById(routeId).ifPresent(route -> model.addAttribute("route", route));
        }

        return "stationManager/schedule/report-results";
    }
}