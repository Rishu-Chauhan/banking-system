package com.bankingsystem.bankingsystem.service;

import com.bankingsystem.bankingsystem.model.User;
import com.bankingsystem.bankingsystem.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;

    // Generate and send OTP
    public void generateAndSendOtp(String username) {
        System.out.println("🔍 Attempting to generate OTP for username: " + username);

        // ✅ 1. Fetch user from database
        User user = userRepo.findByUsername(username);

        if (user == null) {
            System.out.println("❌ No user found with username: " + username);
            return; // stop if user not found
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            System.out.println("❌ User email is missing for username: " + username);
            return; // stop if no email is available
        }

        // ✅ 2. Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepo.save(user);

        // ✅ 3. Prepare email
        String subject = "Your Banking App OTP Code";
        String message = "Dear " + user.getUsername() + ",\n\nYour OTP is: " + otp +
                "\nIt will expire in 2 minutes.\n\nBest regards,\nBanking System";

        // ✅ 4. Send email
        try {
            emailService.sendEmail(user.getEmail(), subject, message);
            System.out.println("📧 OTP sent successfully to: " + user.getEmail());
        } catch (Exception e) {
            System.out.println("❌ Failed to send OTP email: " + e.getMessage());
        }
    }

    // ✅ Verify OTP
    public boolean verifyOtp(String username, String enteredOtp) {
        User user = userRepo.findByUsername(username);
        if (user == null) return false;

        if (user.getOtp() == null) return false;

        // OTP expires in 2 minutes
        LocalDateTime expiryTime = user.getOtpGeneratedTime().plusMinutes(2);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            System.out.println("⚠️ OTP expired for user: " + username);
            return false;
        }

        boolean isValid = user.getOtp().equals(enteredOtp);
        if (isValid) {
            // clear OTP after successful verification
            user.setOtp(null);
            user.setOtpGeneratedTime(null);
            userRepo.save(user);
        }
        return isValid;
    }
}
