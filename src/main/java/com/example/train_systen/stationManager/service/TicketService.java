package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Ticket;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TicketService {

    List<Ticket> getAllTickets();

    Optional<Ticket> getTicketById(Long id);

    Ticket saveTicket(Ticket ticket);

    void deleteTicket(Long id);

    List<Ticket> getTicketsByStatus(String status);

    List<Ticket> getTicketsByDate(LocalDate date);

    List<Ticket> getTicketsByDateRange(LocalDate startDate, LocalDate endDate);

    List<Ticket> getTicketsByRouteId(Long routeId);

    List<Ticket> getTicketsByPassengerName(String passengerName);

    List<Ticket> getTicketsByRouteAndDate(Long routeId, LocalDate travelDate);

    boolean isTicketIdExists(String ticketId);

    // Add this method signature to the TicketService interface
    boolean isSeatBooked(Long routeId, LocalDate travelDate, String seatNumber);

    String generateTicketId();
}