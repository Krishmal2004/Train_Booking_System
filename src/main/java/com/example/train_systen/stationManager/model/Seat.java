package com.example.train_systen.stationManager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coach_number", nullable = false)
    private String coachNumber;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "seat_type", nullable = false)
    private String seatType;

    @Column(name = "ticket_class", nullable = false)
    private String ticketClass;

    @Column(nullable = false)
    private BigDecimal price;

    // --- FIX IS HERE ---
    // This adds the missing relationship back to the Compartment entity.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compartment_id")
    private Compartment compartment;
    // --- END FIX ---


    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCoachNumber() { return coachNumber; }
    public void setCoachNumber(String coachNumber) { this.coachNumber = coachNumber; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    public String getTicketClass() { return ticketClass; }
    public void setTicketClass(String ticketClass) { this.ticketClass = ticketClass; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    // Add getter and setter for the new compartment field
    public Compartment getCompartment() { return compartment; }
    public void setCompartment(Compartment compartment) { this.compartment = compartment; }
}