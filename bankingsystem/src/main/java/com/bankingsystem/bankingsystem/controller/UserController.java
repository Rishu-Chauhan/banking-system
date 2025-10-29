package com.bankingsystem.bankingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/dashboard")
    public String showUserDashboard(Model model) {
        model.addAttribute("title", "User Dashboard");
        model.addAttribute("message", "Welcome to your banking dashboard!");
        return "user_dashboard";  // ✅ make sure user_dashboard.html exists in templates
    }
}
