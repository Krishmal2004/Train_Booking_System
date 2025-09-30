package com.example.train_systen.stationManager.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "trains")
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trainName;
    private String model;
    private int capacity;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Compartment> compartments;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public List<Compartment> getCompartments() { return compartments; }
    public void setCompartments(List<Compartment> compartments) { this.compartments = compartments; }
}