package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.repo.AccountRepository;
import com.bankingsystem.bankingsystem.repo.CustomerRepository;
import com.bankingsystem.bankingsystem.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        long totalCustomers = customerRepo.count();
        long totalAccounts = accountRepo.count();
        long totalTransactions = transactionRepo.count();
        Double totalMoney = accountRepo.sumAllBalances();

        if (totalMoney == null) totalMoney = 0.0;

        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalAccounts", totalAccounts);
        model.addAttribute("totalTransactions", totalTransactions);
        model.addAttribute("totalMoney", totalMoney);

        return "admin_dashboard";
    }
}
