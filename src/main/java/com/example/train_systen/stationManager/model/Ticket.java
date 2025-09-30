package com.example.train_systen.stationManager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotBlank(message = "Ticket ID is required")
    @Column(name = "ticket_id", unique = true)
    private String ticketId;

    @NotBlank(message = "Passenger name is required")
    @Column(name = "passenger_name")
    private String passengerName;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "route_id", referencedColumnName = "id")
    private Route route;  // Changed to optional

    @NotNull(message = "Travel date is required")
    @Column(name = "travel_date")
    private LocalDate travelDate;

    @NotBlank(message = "Seat number is required")
    @Column(name = "seat_number")
    private String seatNumber;

    @NotBlank(message = "Ticket class is required")
    @Column(name = "ticket_class")
    private String ticketClass;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @NotBlank(message = "Status is required")
    private String status = "confirmed"; // Default status: confirmed, pending, cancelled

    private String remarks;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getTicketClass() {
        return ticketClass;
    }

    public void setTicketClass(String ticketClass) {
        this.ticketClass = ticketClass;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", ticketId='" + ticketId + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", route=" + (route != null ? route.getRouteId() : "null") +
                ", travelDate=" + travelDate +
                ", seatNumber='" + seatNumber + '\'' +
                ", ticketClass='" + ticketClass + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}