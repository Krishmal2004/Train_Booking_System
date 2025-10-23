package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Ticket;

public interface EmailService {
    /**
     * Sends a payment confirmation email to the ticket holder
     * @param ticket The ticket details
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendPaymentConfirmation(Ticket ticket);
}