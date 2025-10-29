package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.model.User;
import com.bankingsystem.bankingsystem.repo.UserRepository;
import com.bankingsystem.bankingsystem.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class OtpController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;

    // ✅ Show OTP verification page
    @GetMapping("/verify-otp")
    public String showOtpPage(@RequestParam String user, Model model) {
        model.addAttribute("username", user);
        return "verify_otp";
    }

    // ✅ Handle OTP submission
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String username,
                            @RequestParam String otp,
                            Model model) {

        User user = userRepo.findByUsername(username);

        // ❌ User not found
        if (user == null) {
            model.addAttribute("error", "User not found.");
            model.addAttribute("username", username);
            return "verify_otp";
        }

        // ❌ OTP missing or expired
        if (user.getOtp() == null || user.getOtpGeneratedTime() == null) {
            model.addAttribute("error", "OTP not generated. Please login again.");
            model.addAttribute("username", username);
            return "verify_otp";
        }

        // ❌ OTP expired (valid for 2 minutes)
        if (user.getOtpGeneratedTime().plusMinutes(2).isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "OTP expired. Please login again.");
            model.addAttribute("username", username);
            return "verify_otp";
        }

        // ❌ Invalid OTP
        if (!otp.trim().equals(user.getOtp())) {
            model.addAttribute("error", "Invalid OTP. Please try again.");
            model.addAttribute("username", username);
            return "verify_otp";
        }

        // ✅ OTP verified successfully
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepo.save(user);

        // ✅ Role-based redirect
        String role = user.getRole() != null ? user.getRole().toUpperCase() : "";

        if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
            return "redirect:/account/admin/dashboard";
        }
        else if (role.equals("CUSTOMER") || role.equals("USER") || role.equals("ROLE_CUSTOMER") || role.equals("ROLE_USER")) {
            return "redirect:/user/dashboard";
        }
        else {
            // Default fallback
            return "redirect:/login?error=unknown-role";
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String username, Model model) {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            model.addAttribute("error", "User not found. Please login again.");
            return "verify_otp";
        }

        // ✅ Generate new OTP
        String newOtp = String.format("%06d", new java.util.Random().nextInt(999999));
        user.setOtp(newOtp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepo.save(user);

        // ✅ Send email
        String subject = "Your New OTP Code";
        String message = "Dear " + user.getUsername() + ",\n\nYour new OTP is: " + newOtp +
                "\nIt will expire in 2 minutes.\n\nBanking System";
        emailService.sendEmail(user.getEmail(), subject, message);

        model.addAttribute("success", "A new OTP has been sent to your registered email!");
        model.addAttribute("username", username);
        return "verify_otp";
    }


}
