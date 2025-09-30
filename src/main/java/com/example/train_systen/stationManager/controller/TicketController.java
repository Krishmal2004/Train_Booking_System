package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.model.Route;
import com.example.train_systen.stationManager.model.Ticket;
import com.example.train_systen.stationManager.model.User;
import com.example.train_systen.stationManager.service.RouteService;
import com.example.train_systen.stationManager.service.TicketService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    private final TicketService ticketService;
    private final RouteService routeService;

    @Autowired
    public TicketController(TicketService ticketService, RouteService routeService) {
        this.ticketService = ticketService;
        this.routeService = routeService;
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // ... listTickets method is unchanged ...
    @GetMapping
    public String listTickets(Model model, HttpSession session,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate,
                              @RequestParam(required = false) Long routeId,
                              @RequestParam(required = false) String passengerName) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        logger.info("Accessing ticket list with filters by user: {}", session.getAttribute("username"));

        try {
            List<Ticket> tickets;

            if (status != null && !status.isEmpty()) {
                tickets = ticketService.getTicketsByStatus(status);
            } else if (travelDate != null) {
                tickets = ticketService.getTicketsByDate(travelDate);
            } else if (routeId != null) {
                tickets = ticketService.getTicketsByRouteId(routeId);
            } else if (passengerName != null && !passengerName.isEmpty()) {
                tickets = ticketService.getTicketsByPassengerName(passengerName);
            } else {
                tickets = ticketService.getAllTickets();
            }

            model.addAttribute("tickets", tickets);
            model.addAttribute("currentUser", session.getAttribute("username"));
            model.addAttribute("currentDateTime", getCurrentDateTime());
            return "passenger/ticket/list";

        } catch (Exception ex) {
            logger.error("Error in listTickets: {}", ex.getMessage(), ex);
            model.addAttribute("errorMessage", "Failed to load tickets: " + ex.getMessage());
            return "error";
        }
    }


    @GetMapping("/create")
    public String showCreateForm(@ModelAttribute("ticket") Ticket ticket, Model model, HttpSession session) {
        // This method receives the "ticket" object from the flash attributes,
        // which already contains the travel date.
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        String username = (String) session.getAttribute("username");

        if (ticket.getPassengerName() == null || ticket.getPassengerName().isEmpty()) {
            ticket.setPassengerName(username);
        }

        model.addAttribute("routes", routeService.getAllRoutes());
        model.addAttribute("currentUser", username);
        model.addAttribute("currentDateTime", getCurrentDateTime());
        return "passenger/ticket/create";
    }


    @PostMapping("/create")
    public String createTicket(@Valid @ModelAttribute("ticket") Ticket ticket,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model, HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        String username = (String) session.getAttribute("username");
        logger.info("Processing ticket creation by user: {}", username);

        // --- NEW ---
        // Ensure the passenger name from the session is set, overriding any form input
        ticket.setPassengerName(username);
        // --- END NEW ---

        if (result.hasErrors()) {
            model.addAttribute("routes", routeService.getAllRoutes());
            model.addAttribute("currentUser", username);
            model.addAttribute("currentDateTime", getCurrentDateTime());
            return "passenger/ticket/create";
        }

        ticketService.saveTicket(ticket);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket created successfully for " + username);
        return "redirect:/dashboard";
    }

    @GetMapping("/select-seat")
    public String handleSeatSelection(
            @RequestParam("routeId") Long routeId,
            @RequestParam("travelDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate,
            @RequestParam("ticketClass") String ticketClass,
            @RequestParam("seatNumber") String seatNumber,
            @RequestParam("price") BigDecimal price,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        String username = (String) session.getAttribute("username");
        Route route = routeService.getRouteById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid route ID: " + routeId));

        Ticket ticket = new Ticket();
        ticket.setRoute(route);
        ticket.setTravelDate(travelDate); // <-- The date is set on the object here
        ticket.setTicketClass(ticketClass);
        ticket.setSeatNumber(seatNumber);
        ticket.setPrice(price);
        ticket.setStatus("confirmed");
        ticket.setPassengerName(username);

        // This line makes the ticket object available after the redirect
        redirectAttributes.addFlashAttribute("ticket", ticket);

        return "redirect:/tickets/create";
    }

    // ... viewTicket, showEditForm, updateTicket, and deleteTicket methods are unchanged ...

    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        logger.info("Viewing ticket id: {} by user: {}", id, session.getAttribute("username"));

        Ticket ticket = ticketService.getTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID: " + id));
        model.addAttribute("ticket", ticket);
        model.addAttribute("currentUser", session.getAttribute("username"));
        model.addAttribute("currentDateTime", getCurrentDateTime());
        return "passenger/ticket/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        logger.info("Showing edit form for ticket id: {} by user: {}", id, session.getAttribute("username"));

        Ticket ticket = ticketService.getTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID: " + id));
        model.addAttribute("ticket", ticket);
        model.addAttribute("routes", routeService.getAllRoutes());
        model.addAttribute("currentUser", session.getAttribute("username"));
        model.addAttribute("currentDateTime", getCurrentDateTime());
        return "passenger/ticket/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTicket(@PathVariable Long id,
                               @Valid @ModelAttribute("ticket") Ticket ticket,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model, HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        logger.info("Processing ticket update for id: {} by user: {}", id, session.getAttribute("username"));

        if (result.hasErrors()) {
            model.addAttribute("routes", routeService.getAllRoutes());
            model.addAttribute("currentUser", session.getAttribute("username"));
            model.addAttribute("currentDateTime", getCurrentDateTime());
            return "passenger/ticket/edit";
        }

        ticket.setId(id);
        ticketService.saveTicket(ticket);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket updated successfully");

        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/delete")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        logger.info("Processing ticket DELETION for id: {} by user: {}", id, session.getAttribute("username"));

        ticketService.deleteTicket(id);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket has been permanently deleted.");

        return "redirect:/dashboard";
    }
}