package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Booking;
import com.example.train_systen.stationManager.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Booking createBooking(Booking booking) {
        // You could add validation logic here (e.g., check if seat is already booked)
        return bookingRepository.save(booking);
    }
}