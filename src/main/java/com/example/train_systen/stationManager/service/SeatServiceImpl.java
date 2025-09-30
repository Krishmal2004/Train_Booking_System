package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Seat;
import com.example.train_systen.stationManager.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; // Import Optional

@Service
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    @Autowired
    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public List<Seat> getSeatsByClass(String ticketClass) {
        return seatRepository.findByTicketClassOrderByCoachNumberAscSeatNumberAsc(ticketClass);
    }

    // --- ADD THIS IMPLEMENTATION ---
    @Override
    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }
}