package com.example.train_systen.stationManager.repository;

import com.example.train_systen.stationManager.model.Compartment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompartmentRepository extends JpaRepository<Compartment, Long> {
    List<Compartment> findByTrainId(Long trainId);
}