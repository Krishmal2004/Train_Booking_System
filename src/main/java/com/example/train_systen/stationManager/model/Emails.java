package com.example.train_systen.stationManager.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Emails {

    private String to;
    private String subject;
    private String passengerName;
    private String ticketId;
    private String routeOrigin;
    private String routeDestination;
    private LocalDate travelDate;
    private String seatNumber;
    private BigDecimal price;

    // Constructor to easily create an Email object from a Ticket object
    public Emails(Ticket ticket) {
        // ASSUMPTION: The passenger's name field holds their email address for this example.
        this.to = ticket.getPassengerName();
        this.subject = "✅ Your Ticket is Confirmed! Ticket ID: " + ticket.getTicketId();
        this.passengerName = ticket.getPassengerName();
        this.ticketId = ticket.getTicketId();
        if (ticket.getRoute() != null) {
            this.routeOrigin = ticket.getRoute().getOrigin();
            this.routeDestination = ticket.getRoute().getDestination();
        }
        this.travelDate = ticket.getTravelDate();
        this.seatNumber = ticket.getSeatNumber();
        this.price = ticket.getPrice();
    }

    // Getters and Setters
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public String getRouteOrigin() { return routeOrigin; }
    public void setRouteOrigin(String routeOrigin) { this.routeOrigin = routeOrigin; }
    public String getRouteDestination() { return routeDestination; }
    public void setRouteDestination(String routeDestination) { this.routeDestination = routeDestination; }
    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}