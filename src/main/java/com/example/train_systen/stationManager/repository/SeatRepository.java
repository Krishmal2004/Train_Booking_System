package com.example.train_systen.stationManager.repository;

import com.example.train_systen.stationManager.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByTicketClassOrderByCoachNumberAscSeatNumberAsc(String ticketClass);
}