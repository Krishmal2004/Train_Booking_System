package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:NOT_CONFIGURED}")
    private String mailUsername;

    @Value("${spring.mail.host:NOT_CONFIGURED}")
    private String mailHost;

    @Value("${spring.mail.port:0}")
    private String mailPort;

    @Override
    public boolean sendPaymentConfirmation(Ticket ticket) {
        logger.info("========== EMAIL SENDING ATTEMPT ==========");
        logger.info("Ticket ID: {}", ticket.getTicketId());
        logger.info("Passenger Name: {}", ticket.getPassengerName());
        logger.info("Passenger Email: {}", ticket.getPassengerEmail());

        // Declare recipientEmail at the beginning
        String recipientEmail = ticket.getPassengerEmail();

        try {
            // Check 1: Validate recipient email
            if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                logger.error("❌ FAILED: No recipient email address available");
                return false;
            }
            logger.info("✓ Recipient email validated: {}", recipientEmail);

            // Check 2: Verify email sender is configured
            if (mailSender == null) {
                logger.error("❌ FAILED: JavaMailSender is NULL - Email configuration missing!");
                logger.error("Please configure email properties in application.properties");
                System.out.println("\n=== EMAIL NOT SENT - CONFIGURATION MISSING ===");
                System.out.println("To: " + recipientEmail);
                System.out.println("Subject: Ticket Confirmation - " + ticket.getTicketId());
                System.out.println("==============================================\n");
                return false;
            }
            logger.info("✓ JavaMailSender is configured");

            // Check 3: Log email configuration
            logger.info("Email Configuration:");
            logger.info("  Host: {}", mailHost);
            logger.info("  Port: {}", mailPort);
            logger.info("  Username: {}", mailUsername);

            // Check 4: Generate QR Code as byte array
            logger.info("Generating QR code...");
            byte[] qrCodeBytes = generateQRCodeBytes(ticket);
            logger.info("✓ QR code generated successfully ({} bytes)", qrCodeBytes.length);

            // Check 5: Create HTML email message with multipart
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setFrom(mailUsername);
            helper.setSubject("✅ Your Ticket is Confirmed! - " + ticket.getTicketId());

            // Generate HTML email content
            String htmlContent = generateHtmlTicketEmail(ticket, recipientEmail);
            helper.setText(htmlContent, true); // true indicates HTML

            // Add QR code as inline image
            helper.addInline("qrCode", new ByteArrayResource(qrCodeBytes), "image/png");
            logger.info("✓ QR code attached as inline image");

            logger.info("Attempting to send HTML email with QR code...");
            mailSender.send(mimeMessage);

            logger.info("✅ SUCCESS: HTML Email with QR code sent to {}", recipientEmail);
            logger.info("==========================================");
            return true;

        } catch (MessagingException e) {
            logger.error("❌ MESSAGING EXCEPTION: {}", e.getMessage());
            logger.error("Exception Type: {}", e.getClass().getName());
            logger.error("Stack trace:", e);
            logger.error("==========================================");
            return false;

        } catch (MailException e) {
            logger.error("❌ MAIL EXCEPTION: {}", e.getMessage());
            logger.error("Exception Type: {}", e.getClass().getName());
            logger.error("Stack trace:", e);
            logger.error("==========================================");
            return false;

        } catch (Exception e) {
            logger.error("❌ UNEXPECTED EXCEPTION: {}", e.getMessage());
            logger.error("Exception Type: {}", e.getClass().getName());
            logger.error("Stack trace:", e);
            logger.error("==========================================");
            return false;
        }
    }

    /**
     * Generates QR code for the ticket and returns it as byte array
     */
    private byte[] generateQRCodeBytes(Ticket ticket) throws WriterException, IOException {
        // Create QR code content with ticket information
        String qrContent = String.format(
                "TrainConnect Ticket\n" +
                        "Ticket ID: %s\n" +
                        "Passenger: %s\n" +
                        "From: %s\n" +
                        "To: %s\n" +
                        "Date: %s\n" +
                        "Seat: %s\n" +
                        "Class: %s\n" +
                        "Price: $%.2f\n" +
                        "Status: %s",
                ticket.getTicketId(),
                ticket.getPassengerName(),
                ticket.getRoute().getOrigin(),
                ticket.getRoute().getDestination(),
                ticket.getTravelDate().toString(),
                ticket.getSeatNumber(),
                ticket.getTicketClass(),
                ticket.getPrice(),
                ticket.getStatus()
        );

        logger.info("QR Code Content: {}", qrContent);

        // Generate QR Code with larger size for better scanning
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 400, 400);

        // Convert BitMatrix to BufferedImage
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Convert BufferedImage to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        return baos.toByteArray();
    }

    /**
     * Generates a beautiful HTML email template for the ticket with QR code
     */
    private String generateHtmlTicketEmail(Ticket ticket, String recipientEmail) {
        // Format the travel date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String formattedDate = ticket.getTravelDate() != null
                ? ticket.getTravelDate().format(dateFormatter)
                : "Not specified";

        // Get origin and destination codes (first 3 letters)
        String originCode = ticket.getRoute().getOrigin().substring(0, Math.min(3, ticket.getRoute().getOrigin().length())).toUpperCase();
        String destinationCode = ticket.getRoute().getDestination().substring(0, Math.min(3, ticket.getRoute().getDestination().length())).toUpperCase();

        // Determine class styling
        String classColor = "#198754"; // Default green for economy
        String classBgColor = "rgba(25, 135, 84, 0.1)";
        if ("business".equalsIgnoreCase(ticket.getTicketClass())) {
            classColor = "#0dcaf0";
            classBgColor = "rgba(13, 202, 240, 0.1)";
        } else if ("first".equalsIgnoreCase(ticket.getTicketClass())) {
            classColor = "#e63946";
            classBgColor = "rgba(230, 57, 70, 0.1)";
        }

        // Determine status styling
        String statusColor = "#198754"; // Confirmed
        String statusBgColor = "rgba(25, 135, 84, 0.1)";
        if ("pending".equalsIgnoreCase(ticket.getStatus())) {
            statusColor = "#ffc107";
            statusBgColor = "rgba(255, 193, 7, 0.1)";
        } else if ("cancelled".equalsIgnoreCase(ticket.getStatus())) {
            statusColor = "#dc3545";
            statusBgColor = "rgba(220, 53, 69, 0.1)";
        }

        return String.format("""
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f7fa;
            margin: 0;
            padding: 20px;
        }
        .email-container {
            max-width: 600px;
            margin: 0 auto;
            background-color: #ffffff;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        }
        .header {
            background: linear-gradient(135deg, #1a3a6e 0%%, #2d5aa0 100%%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 28px;
            font-weight: 700;
        }
        .header p {
            margin: 10px 0 0 0;
            font-size: 14px;
            opacity: 0.9;
        }
        .ticket-card {
            background: linear-gradient(135deg, #f8f9fa 0%%, #e9ecef 100%%);
            margin: 20px;
            padding: 30px;
            border-radius: 12px;
            position: relative;
        }
        .ticket-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 2px dashed #ced4da;
        }
        .logo {
            font-size: 24px;
            font-weight: 700;
            color: #1a3a6e;
        }
        .ticket-id {
            background-color: rgba(26, 58, 110, 0.1);
            color: #1a3a6e;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: 600;
            font-size: 14px;
        }
        .route-section {
            text-align: center;
            margin: 25px 0;
            position: relative;
        }
        .route-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            position: relative;
        }
        .station {
            flex: 1;
            text-align: center;
        }
        .station-code {
            font-size: 32px;
            font-weight: 700;
            color: #212529;
            margin-bottom: 5px;
        }
        .station-name {
            font-size: 12px;
            color: #6c757d;
            text-transform: uppercase;
        }
        .route-line {
            flex: 2;
            height: 2px;
            background: repeating-linear-gradient(
                90deg,
                #1a3a6e,
                #1a3a6e 5px,
                transparent 5px,
                transparent 12px
            );
            margin: 0 20px;
            position: relative;
        }
        .train-icon {
            position: absolute;
            top: 50%%;
            left: 50%%;
            transform: translate(-50%%, -50%%);
            background-color: white;
            width: 35px;
            height: 35px;
            border-radius: 50%%;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            font-size: 18px;
        }
        .details-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
            margin: 25px 0;
        }
        .detail-item {
            background-color: white;
            padding: 15px;
            border-radius: 8px;
        }
        .detail-label {
            font-size: 11px;
            color: #6c757d;
            text-transform: uppercase;
            font-weight: 600;
            margin-bottom: 5px;
        }
        .detail-value {
            font-size: 16px;
            color: #212529;
            font-weight: 600;
        }
        .badge {
            display: inline-block;
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
        }
        .ticket-footer {
            margin-top: 25px;
            padding-top: 20px;
            border-top: 2px dashed #ced4da;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
        }
        .qr-section {
            text-align: center;
            flex: 1;
            min-width: 200px;
        }
        .qr-code {
            max-width: 200px;
            height: auto;
            margin: 10px auto;
            padding: 10px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            display: block;
        }
        .qr-instruction {
            font-size: 12px;
            color: #6c757d;
            margin-top: 10px;
            font-weight: 500;
        }
        .price-section {
            text-align: center;
            flex: 1;
            min-width: 150px;
        }
        .price {
            font-size: 32px;
            font-weight: 700;
            color: #1a3a6e;
        }
        .price-label {
            font-size: 12px;
            color: #6c757d;
            text-transform: uppercase;
            font-weight: 600;
            margin-top: 5px;
        }
        .content {
            padding: 30px;
            color: #333;
            line-height: 1.6;
        }
        .content h2 {
            color: #1a3a6e;
            margin-top: 0;
        }
        .content ul {
            padding-left: 20px;
        }
        .content li {
            margin-bottom: 10px;
        }
        .footer {
            background-color: #f8f9fa;
            padding: 20px;
            text-align: center;
            color: #6c757d;
            font-size: 12px;
        }
        @media only screen and (max-width: 600px) {
            .details-grid {
                grid-template-columns: 1fr;
            }
            .route-row {
                flex-direction: column;
            }
            .route-line {
                width: 2px;
                height: 40px;
                margin: 10px 0;
            }
            .ticket-footer {
                flex-direction: column;
                gap: 20px;
            }
        }
    </style>
</head>
<body>
    <div class="email-container">
        <div class="header">
            <h1>🚂 TrainConnect</h1>
            <p>Your ticket has been confirmed!</p>
        </div>

        <div class="ticket-card">
            <div class="ticket-header">
                <div class="logo">🚂 TrainConnect</div>
                <div class="ticket-id">%s</div>
            </div>

            <div class="route-section">
                <div class="route-row">
                    <div class="station">
                        <div class="station-code">%s</div>
                        <div class="station-name">Origin</div>
                    </div>
                    <div class="route-line">
                        <div class="train-icon">🚂</div>
                    </div>
                    <div class="station">
                        <div class="station-code">%s</div>
                        <div class="station-name">Destination</div>
                    </div>
                </div>
            </div>

            <div class="details-grid">
                <div class="detail-item">
                    <div class="detail-label">Passenger</div>
                    <div class="detail-value">%s</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Email</div>
                    <div class="detail-value" style="font-size: 13px;">%s</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Travel Date</div>
                    <div class="detail-value">%s</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Seat</div>
                    <div class="detail-value">%s</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Class</div>
                    <div class="badge" style="background-color: %s; color: %s;">%s</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Status</div>
                    <div class="badge" style="background-color: %s; color: %s;">%s</div>
                </div>
            </div>

            <div class="ticket-footer">
                <div class="qr-section">
                    <img src="cid:qrCode" alt="Ticket QR Code" class="qr-code">
                    <p class="qr-instruction">📱 Scan at station for quick check-in</p>
                </div>
                <div class="price-section">
                    <div class="price">$%.2f</div>
                    <div class="price-label">Total Amount</div>
                </div>
            </div>
        </div>

        <div class="content">
            <h2>📋 Important Information</h2>
            <p>Dear %s,</p>
            <p>Your payment was successful and your ticket has been confirmed!</p>
            <ul>
                <li>📍 Please arrive at <strong>%s</strong> station at least <strong>30 minutes before departure</strong></li>
                <li>🆔 Carry a valid ID proof for verification</li>
                <li>📱 <strong>Show the QR code</strong> above at the station for quick check-in</li>
                <li>💾 Keep this email for your records</li>
                <li>🪑 Your seat number is <strong>%s</strong></li>
                <li>🎫 Your ticket class: <strong>%s</strong></li>
            </ul>
            <p><strong>Note:</strong> This QR code is unique to your booking and can be scanned by our staff for verification.</p>
            <p>Thank you for choosing TrainConnect. We wish you a pleasant journey!</p>
        </div>

        <div class="footer">
            <p><strong>&copy; 2025 TrainConnect. All rights reserved.</strong></p>
            <p>This is an automated email. Please do not reply.</p>
            <p style="margin-top: 10px;">Need help? Contact us at support@trainconnect.com</p>
        </div>
    </div>
</body>
</html>
                """,
                ticket.getTicketId(),
                originCode,
                destinationCode,
                ticket.getPassengerName(),
                recipientEmail,
                formattedDate,
                ticket.getSeatNumber(),
                classBgColor, classColor, ticket.getTicketClass().toUpperCase(),
                statusBgColor, statusColor, ticket.getStatus().toUpperCase(),
                ticket.getPrice(),
                ticket.getPassengerName(),
                ticket.getRoute().getOrigin(),
                ticket.getSeatNumber(),
                ticket.getTicketClass().toUpperCase()
        );
    }
}