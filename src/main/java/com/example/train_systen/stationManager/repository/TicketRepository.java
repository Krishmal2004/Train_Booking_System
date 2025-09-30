package com.example.train_systen.stationManager.repository;

import com.example.train_systen.stationManager.dto.MonthlySalesDTO;
import com.example.train_systen.stationManager.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(String status);

    List<Ticket> findByTravelDate(LocalDate date);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.route WHERE t.travelDate BETWEEN :startDate AND :endDate")
    List<Ticket> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.route WHERE t.route.id = :routeId")
    List<Ticket> findByRouteId(@Param("routeId") Long routeId);

    List<Ticket> findByPassengerNameContainingIgnoreCase(String passengerName);

    boolean existsByTicketId(String ticketId);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.route WHERE t.route.id = :routeId AND t.travelDate = :travelDate")
    List<Ticket> findByRouteAndTravelDate(@Param("routeId") Long routeId, @Param("travelDate") LocalDate travelDate);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.route")
    List<Ticket> findAll();

    @Query("SELECT COALESCE(SUM(t.price), 0) FROM Ticket t")
    BigDecimal findTotalRevenue();

    @Query("SELECT COALESCE(SUM(t.price), 0) FROM Ticket t WHERE t.travelDate >= :startDate AND t.travelDate <= :endDate")
    BigDecimal findRevenueBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.example.train_systen.stationManager.dto.MonthlySalesDTO(MONTH(t.travelDate), SUM(t.price)) " +
            "FROM Ticket t WHERE YEAR(t.travelDate) = :year " +
            "GROUP BY MONTH(t.travelDate) ORDER BY MONTH(t.travelDate) ASC")
    List<MonthlySalesDTO> findMonthlySalesForYear(@Param("year") int year);

    List<Ticket> findByPassengerName(String passengerName);

    List<Ticket> findByTravelDateBetween(LocalDate startDate, LocalDate endDate);

}