package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestEmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/test-email")
    @ResponseBody // so it returns text instead of looking for a .html file
    public String testEmail() {
        emailService.sendEmail(
                "yourpersonalemail@gmail.com",   // ✅ receiver email
                "Test Mail from Banking System",
                "This is a test email from your Spring Boot Banking System project!"
        );
        return "✅ Test email sent successfully!";
    }
}
