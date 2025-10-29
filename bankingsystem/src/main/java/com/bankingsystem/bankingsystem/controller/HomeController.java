package com.bankingsystem.bankingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        // redirect to account list or show home page
        return "redirect:/account/list";
    }
}