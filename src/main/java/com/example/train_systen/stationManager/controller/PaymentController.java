package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.model.Ticket;
import com.example.train_systen.stationManager.service.EmailService;
import com.example.train_systen.stationManager.service.TicketService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final TicketService ticketService;
    private final EmailService emailService;

    @Autowired
    public PaymentController(TicketService ticketService, EmailService emailService) {
        this.ticketService = ticketService;
        this.emailService = emailService;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Displays the payment form and creates a Stripe Payment Intent.
     */
    @GetMapping("/pay/{ticketId}")
    public String showPaymentForm(@PathVariable Long ticketId, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        Optional<Ticket> ticketOpt = ticketService.getTicketById(ticketId);

        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            String currentUser = (String) session.getAttribute("username");

            if (!ticket.getPassengerName().equals(currentUser)) {
                return "redirect:/dashboard?error=access_denied";
            }

            // Do not allow payment for already confirmed tickets
            if ("confirmed".equalsIgnoreCase(ticket.getStatus())) {
                return "redirect:/dashboard?error=already_paid";
            }

            try {
                // Create a PaymentIntent on the server
                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        // Stripe requires the amount in the smallest currency unit (e.g., cents)
                        .setAmount(ticket.getPrice().multiply(new BigDecimal("100")).longValue())
                        .setCurrency("usd") // Change to your desired currency
                        .putMetadata("ticketId", ticket.getId().toString())
                        .putMetadata("passenger", ticket.getPassengerName())
                        .build();

                PaymentIntent paymentIntent = PaymentIntent.create(params);

                model.addAttribute("ticket", ticket);
                // Pass the Payment Intent's client secret to the frontend
                model.addAttribute("clientSecret", paymentIntent.getClientSecret());
                return "payment";

            } catch (StripeException e) {
                model.addAttribute("errorMessage", "Could not initialize payment. Please try again.");
                logger.error("Stripe error: {}", e.getMessage(), e);
                return "error"; // Or redirect to an error page
            }
        } else {
            return "redirect:/dashboard?error=ticket_not_found";
        }
    }

    /**
     * This endpoint is called by the frontend JS after a successful Stripe payment
     * to update the ticket status in the database and send confirmation email.
     */
    @PostMapping("/charge")
    public String processSuccessfulPayment(@RequestParam Long ticketId, RedirectAttributes redirectAttributes) {
        logger.info("========== PAYMENT PROCESSING STARTED ==========");
        logger.info("Processing payment for ticket ID: {}", ticketId);

        Optional<Ticket> ticketOpt = ticketService.getTicketById(ticketId);

        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();

            // Update ticket status
            ticket.setStatus("confirmed");
            ticketService.saveTicket(ticket);
            logger.info("✓ Ticket status updated to 'confirmed'");

            // Send confirmation email
            logger.info("Attempting to send confirmation email...");
            boolean emailSent = emailService.sendPaymentConfirmation(ticket);

            if (emailSent) {
                logger.info("✅ Email sent successfully!");
                redirectAttributes.addFlashAttribute("successMessage",
                        "Payment successful! Your ticket is confirmed and a confirmation email has been sent to " + ticket.getPassengerEmail());
            } else {
                logger.warn("⚠️ Email failed to send, but ticket is confirmed");
                redirectAttributes.addFlashAttribute("successMessage",
                        "Payment successful! Your ticket is confirmed.");
                redirectAttributes.addFlashAttribute("warningMessage",
                        "Note: Confirmation email could not be sent. Please check your email settings.");
            }

            logger.info("========== PAYMENT PROCESSING COMPLETED ==========");
            return "redirect:/dashboard";
        }

        logger.error("❌ Ticket not found with ID: {}", ticketId);
        redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while confirming your ticket.");
        return "redirect:/dashboard";
    }
}