package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.model.Schedule;
import com.example.train_systen.stationManager.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class schedulePageController {

    private final ScheduleService scheduleService;

    @Autowired
    public schedulePageController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/schedule")
    public String showSchedulesPage(Model model) {
        // 1. This will now fetch all schedules from the service
        List<Schedule> allSchedules = scheduleService.getAllSchedules();

        // 2. This will now add the list to the model
        model.addAttribute("schedules", allSchedules);

        // 3. This will now return the name of the HTML template to render
        return "schedules";
    }
}