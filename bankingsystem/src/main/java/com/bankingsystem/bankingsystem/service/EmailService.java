package com.bankingsystem.bankingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("rishuchauhan798@gmail.com"); // your configured email
        mailSender.send(message);
    }

    public void sendOtpEmail(String email, String newOtp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("🔐 OTP Verification - Banking System");
            message.setText("Dear User,\n\n"
                    + "Your One-Time Password (OTP) for login verification is: " + newOtp
                    + "\n\nThis OTP is valid for 2 minutes.\n\n"
                    + "If you did not request this, please ignore this email.\n\n"
                    + "Best Regards,\nBanking System Security Team");

            mailSender.send(message);
            System.out.println("✅ OTP email sent successfully to: " + email);

        } catch (Exception e) {
            System.err.println("❌ Failed to send OTP email: " + e.getMessage());
        }
    }
}
