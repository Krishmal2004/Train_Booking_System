package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Seat;
import java.util.List;
import java.util.Optional; // Import Optional

public interface SeatService {
    List<Seat> getSeatsByClass(String ticketClass);

    // --- ADD THIS METHOD ---
    Optional<Seat> getSeatById(Long id);
}