package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Ticket;
import com.example.train_systen.stationManager.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<Ticket> getAllTickets() {
        try {
            return ticketRepository.findAll();
        } catch (DataAccessException e) {
            logger.error("Error fetching all tickets: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // Add this method implementation to TicketServiceImpl.java

    @Override
    public boolean isSeatBooked(Long routeId, LocalDate travelDate, String seatNumber) {
        // This check is the core of the validation logic.
        return ticketRepository.existsByRouteIdAndTravelDateAndSeatNumber(routeId, travelDate, seatNumber);
    }

    @Override
    public Optional<Ticket> getTicketById(Long id) {
        try {
            return ticketRepository.findById(id);
        } catch (DataAccessException e) {
            logger.error("Error fetching ticket by id {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Ticket saveTicket(Ticket ticket) {
        try {
            // =======================================================================
            //                              CHANGE IS HERE
            // =======================================================================
            // OLD LOGIC: if (ticket.getTicketId() == null || ticket.getTicketId().isEmpty())
            // NEW LOGIC: We check if the ticket is new by seeing if its primary key (id) is null.
            // If it is a new ticket, we ALWAYS generate a new ID, ignoring any user input from the form.
            if (ticket.getId() == null) {
                ticket.setTicketId(generateTicketId());
            }
            // =======================================================================

            return ticketRepository.save(ticket);
        } catch (DataAccessException e) {
            logger.error("Error saving ticket: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteTicket(Long id) {
        try {
            ticketRepository.deleteById(id);
        } catch (DataAccessException e) {
            logger.error("Error deleting ticket with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Ticket> getTicketsByStatus(String status) {
        try {
            return ticketRepository.findByStatus(status);
        } catch (DataAccessException e) {
            logger.error("Error fetching tickets by status {}: {}", status, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Ticket> getTicketsByDate(LocalDate date) {
        try {
            return ticketRepository.findByTravelDate(date);
        } catch (DataAccessException e) {
            logger.error("Error fetching tickets by date {}: {}", date, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Ticket> getTicketsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return ticketRepository.findByDateRange(startDate, endDate);
        } catch (DataAccessException e) {
            logger.error("Error fetching tickets by date range {} to {}: {}",
                    startDate, endDate, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Ticket> getTicketsByRouteId(Long routeId) {
        try {
            return ticketRepository.findByRouteId(routeId);
        } catch (DataAccessException e) {
            logger.error("Error fetching tickets by route id {}: {}", routeId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Ticket> getTicketsByPassengerName(String passengerName) {
        try {
            return ticketRepository.findByPassengerNameIgnoreCase(passengerName);
        } catch (DataAccessException e) {
            logger.error("Error fetching tickets by passenger name {}: {}",
                    passengerName, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Ticket> getTicketsByRouteAndDate(Long routeId, LocalDate travelDate) {
        try {
            return ticketRepository.findByRouteAndTravelDate(routeId, travelDate);
        } catch (DataAccessException e) {
            logger.error("Error fetching tickets by route id {} and date {}: {}",
                    routeId, travelDate, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean isTicketIdExists(String ticketId) {
        try {
            return ticketRepository.existsByTicketId(ticketId);
        } catch (DataAccessException e) {
            logger.error("Error checking if ticket id exists {}: {}", ticketId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String generateTicketId() {
        LocalDate now = LocalDate.now();
        String datePrefix = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Random random = new Random();
        String randomSuffix = String.format("%04d", random.nextInt(10000));
        String ticketId = "TKT-" + datePrefix + "-" + randomSuffix;

        while (isTicketIdExists(ticketId)) {
            randomSuffix = String.format("%04d", random.nextInt(10000));
            ticketId = "TKT-" + datePrefix + "-" + randomSuffix;
        }

        return ticketId;
    }
}