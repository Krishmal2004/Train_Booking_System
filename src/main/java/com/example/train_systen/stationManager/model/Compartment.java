package com.example.train_systen.stationManager.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "compartments")
public class Compartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String compartmentName;
    private String classType; // Economy, Business, First

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id")
    private Train train;

    @OneToMany(mappedBy = "compartment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompartmentName() { return compartmentName; }
    public void setCompartmentName(String compartmentName) { this.compartmentName = compartmentName; }
    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }
    public Train getTrain() { return train; }
    public void setTrain(Train train) { this.train = train; }
    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
}