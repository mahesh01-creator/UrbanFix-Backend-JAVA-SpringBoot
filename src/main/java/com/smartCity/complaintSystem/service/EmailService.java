package com.smartCity.complaintSystem.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendComplaintStatusEmail(
            String toEmail,
            String userName,
            String complaintTitle,
            String status,
            String imagePath,
            boolean isNewComplaint
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(toEmail);

            helper.setSubject(
                    "Smart City Complaint Update - " + status
            );

            String statusColor = getStatusColor(status);

            String statusMessage =
                    getStatusMessage(
                            status,
                            userName,
                            complaintTitle
                    );

//            String timeline =
//                    buildTimeline(status);

            String currentDate =
                    LocalDateTime.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "dd MMM yyyy hh:mm a"
                                    )
                            );

            String html =

                    "<!DOCTYPE html>" +
                    "<html>" +

                    "<body style='margin:0;padding:0;background:#0f172a;font-family:Arial,sans-serif'>" +

                    "<div style='max-width:700px;margin:auto;background:#111827;border-radius:24px;overflow:hidden;border:1px solid #1f2937'>" +

                    // HEADER
                    "<div style='background:linear-gradient(135deg,#06b6d4,#6366f1,#8b5cf6);padding:50px 30px;text-align:center'>" +

                    "<h1 style='color:white;margin:0;font-size:38px;font-weight:900'>" +
                    "SMART CITY" +
                    "</h1>" +

                    "<p style='color:#e0e7ff;margin-top:10px;font-size:16px'>" +
                    "Intelligent Complaint Management System" +
                    "</p>" +

                    "</div>" +

                    // BODY
                    "<div style='padding:40px'>" +

                    "<h2 style='color:white;font-size:28px;margin-bottom:10px'>" +
                    "Hello " + userName + " 👋" +
                    "</h2>" +

                    "<p style='color:#9ca3af;font-size:16px;line-height:1.8'>" +
                    statusMessage +
                    "</p>" +

                    // COMPLAINT CARD
                    "<div style='margin-top:30px;background:#1f2937;border:1px solid #374151;border-radius:20px;padding:25px'>" +

                    "<p style='color:#9ca3af;font-size:14px;margin-bottom:8px'>" +
                    "Complaint Title" +
                    "</p>" +

                    "<h3 style='color:white;font-size:24px;margin-top:0'>" +
                    complaintTitle +
                    "</h3>" +

                    "<div style='margin-top:20px'>" +

                    "<span style='display:inline-block;padding:12px 22px;border-radius:999px;background:" +
                    statusColor +
                    ";color:white;font-weight:bold;font-size:14px'>" +

                    status +

                    "</span>" +

                    "</div>" +

                    "</div>" +

//                    // TIMELINE
//                    timeline +

                    // PREMIUM MESSAGE
                    "<div style='margin-top:35px;padding:25px;border-radius:20px;background:rgba(99,102,241,0.08);border:1px solid rgba(99,102,241,0.2)'>" +

                    "<h3 style='color:#c4b5fd;margin-top:0'>" +
                    "Why Smart City?" +
                    "</h3>" +

                    "<p style='color:#d1d5db;line-height:1.8'>" +
                    "Our AI-powered civic response platform ensures faster issue tracking, intelligent worker assignment, transparent monitoring, and real-time complaint resolution for a smarter urban experience." +
                    "</p>" +

                    "</div>" +

                    // FOOTER
                    "<div style='margin-top:40px;padding-top:25px;border-top:1px solid #374151'>" +

                    "<p style='color:#6b7280;font-size:14px'>" +
                    "Generated on: " + currentDate +
                    "</p>" +

                    "<p style='color:#9ca3af;font-size:14px;line-height:1.8'>" +
                    "Thank you for helping improve the city. Together we build cleaner, safer and smarter communities 🚀" +
                    "</p>" +

                    "</div>" +

                    "</div>" +

                    "</div>" +

                    "</body>" +
                    "</html>";

            helper.setText(html, true);

            // OPTIONAL IMAGE ATTACHMENT
            if (imagePath != null) {

                FileSystemResource file =
                        new FileSystemResource(
                                new File("uploads/" + imagePath)
                        );

                if (file.exists()) {

                    helper.addAttachment(
                            "complaint-image.jpg",
                            file
                    );
                }
            }

            mailSender.send(message);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================================
    // STATUS COLORS
    // =========================================

    private String getStatusColor(String status) {

        return switch (status) {

            case "PENDING" ->
                    "#f59e0b";

            case "VERIFIED" ->
                    "#3b82f6";

            case "ASSIGNED" ->
                    "#8b5cf6";

            case "IN_PROGRESS" ->
                    "#06b6d4";

            case "RESOLVED" ->
                    "#22c55e";

            case "REJECTED" ->
                    "#ef4444";

            default ->
                    "#6b7280";
        };
    }

    // =========================================
    // STATUS MESSAGE
    // =========================================

    private String getStatusMessage(
            String status,
            String userName,
            String complaintTitle
    ) {

        return switch (status) {

            case "PENDING" ->

                    "Your complaint has been successfully registered in the Smart City Management System. Our moderation team will review the issue shortly. Thank you for reporting and helping improve the city infrastructure.";

            case "VERIFIED" ->

                    "Great news! Your complaint has been verified by the administration team and approved for action. Our intelligent assignment engine will now allocate the most suitable field worker.";

            case "ASSIGNED" ->

                    "Your complaint has now been assigned to a professional field worker. The issue is officially under process and resolution work will begin soon.";

            case "IN_PROGRESS" ->

                    "Resolution work is currently in progress. The assigned field worker is actively handling the reported issue at the location.";

            case "RESOLVED" ->

                    "Your complaint has been successfully resolved. Thank you for contributing toward building a smarter and cleaner city environment.";

            case "REJECTED" ->

                    "After review, the complaint could not be approved by the moderation team. If you believe this was a mistake, please submit additional valid information.";

            default ->

                    "Your complaint status has been updated.";
        };
    }

    // =========================================
    // TIMELINE
    // =========================================

    private String buildTimeline(String currentStatus) {

        String[] steps = {
                "PENDING",
                "VERIFIED",
                "ASSIGNED",
                "IN_PROGRESS",
                "RESOLVED"
        };

        StringBuilder timeline =
                new StringBuilder();

        timeline.append(
                "<div style='margin-top:40px'>"
        );

        timeline.append(
                "<h3 style='color:white;margin-bottom:25px'>Complaint Progress</h3>"
        );

        timeline.append(
                "<div style='display:flex;justify-content:space-between;flex-wrap:wrap;gap:10px'>"
        );

        boolean active = true;

        for (String step : steps) {

            String bg =
                    active
                            ? "#22c55e"
                            : "#374151";

            timeline.append(
                    "<div style='flex:1;min-width:100px;text-align:center'>" +

                    "<div style='width:22px;height:22px;border-radius:50%;margin:auto;background:" +
                    bg +
                    ";box-shadow:0 0 15px " + bg + "'>" +

                    "</div>" +

                    "<p style='color:#d1d5db;font-size:12px;margin-top:10px'>" +
                    step +
                    "</p>" +

                    "</div>"
            );

            if (!step.equals(currentStatus)) {
                active = false;
            }
        }

        timeline.append("</div></div>");

        return timeline.toString();
    }
}

