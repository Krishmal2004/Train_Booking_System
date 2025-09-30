package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.model.Route; // <-- 1. Import Route model
import com.example.train_systen.stationManager.model.Seat;
import com.example.train_systen.stationManager.model.Ticket;
import com.example.train_systen.stationManager.service.RouteService; // <-- 2. Import RouteService
import com.example.train_systen.stationManager.service.SeatService;
import com.example.train_systen.stationManager.service.TicketService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/seats")
public class SeatController {
    private static final Logger logger = LoggerFactory.getLogger(SeatController.class);
    private final SeatService seatService;
    private final TicketService ticketService;
    private final RouteService routeService; // <-- 3. Add RouteService field

    @Autowired
    public SeatController(SeatService seatService, TicketService ticketService, RouteService routeService) { // <-- 4. Inject in constructor
        this.seatService = seatService;
        this.ticketService = ticketService;
        this.routeService = routeService; // <-- 5. Initialize it
    }

    @GetMapping("/select")
    public String showSeatSelection(
            @RequestParam("routeId") Long routeId,
            @RequestParam("travelDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate,
            @RequestParam("ticketClass") String ticketClass,
            Model model, HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        logger.info("Loading seat map for routeId: {}, date: {}, class: {}", routeId, travelDate, ticketClass);

        // --- NEW CHANGES START HERE ---
        // 1. Fetch the selected route object from the database
        Route selectedRoute = routeService.getRouteById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found for id: " + routeId));
        // --- NEW CHANGES END HERE ---

        // 2. Get all seats for the selected class
        List<Seat> allSeats = seatService.getSeatsByClass(ticketClass);

        Map<String, List<Seat>> seatsByCoach = allSeats.stream()
                .collect(Collectors.groupingBy(Seat::getCoachNumber));

        // 3. Get all unavailable seat numbers
        List<String> unavailableSeatNumbers = ticketService.getTicketsByRouteAndDate(routeId, travelDate).stream()
                .map(Ticket::getSeatNumber)
                .collect(Collectors.toList());
        String formattedClass = ticketClass.substring(0, 1).toUpperCase() + ticketClass.substring(1).toLowerCase();

        // 4. Add the route and other details to the model
        model.addAttribute("route", selectedRoute); // <-- 6. Add the full route object to the model
        model.addAttribute("seatsByCoach", seatsByCoach);
        model.addAttribute("unavailableSeats", unavailableSeatNumbers);
        model.addAttribute("routeId", routeId);
        model.addAttribute("travelDate", travelDate);
        model.addAttribute("ticketClass", ticketClass);
        model.addAttribute("formattedTicketClass", formattedClass);
        model.addAttribute("currentUser", session.getAttribute("username"));

        return "passenger/ticket/select-seat";
    }
}