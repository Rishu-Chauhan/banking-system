package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.model.Account;
import com.bankingsystem.bankingsystem.model.Customer;
import com.bankingsystem.bankingsystem.repo.AccountRepository;
import com.bankingsystem.bankingsystem.repo.CustomerRepository;
import com.bankingsystem.bankingsystem.repo.TransactionRepository;
import com.bankingsystem.bankingsystem.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService service;

    @Autowired
    private AccountRepository accountRepository;


    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private CustomerRepository customerRepo;


    @GetMapping("/deposit/{accNo}")
    public String depositForm(@PathVariable String accNo, Model model) {
        model.addAttribute("accNo", accNo);
        return "deposit_form";
    }
    @PostMapping("/deposit")
    public String deposit(@RequestParam String accNo, @RequestParam double amount,Model model) {
//        service.deposit(accNo, amount);
//        return "redirect:/account/list";
        try {
            service.deposit(accNo, amount);
            return "redirect:/account/list";
        } catch (RuntimeException e) {
            // Send error message to the view
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("accounts", service.getAllAccounts());
            return "account_list";
        }
    }

    // Show Withdraw Form
    @GetMapping("/withdraw/{accNo}")
    public String withdrawForm(@PathVariable String accNo, Model model) {
        model.addAttribute("accNo", accNo);
        return "withdraw_form";
    }

    // Handle Withdraw
    @PostMapping("/withdraw")
    public String withdraw(@RequestParam String accNo,
                           @RequestParam double amount,
                           Model model) {
        try {
            service.withdraw(accNo, amount);
            model.addAttribute("message", "Withdrawal successful!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "withdraw_form"; // reload form with error
        }
        return "redirect:/account/list";
    }


    @GetMapping("/open")
    @PreAuthorize("hasRole('ADMIN')")
    public String openForm() {
        return "open_account";
    }

    @PostMapping("/open")
    @PreAuthorize("hasRole('ADMIN')")
    public String openAccount(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String phone,
                              @RequestParam String accountType) {

        // 1. Create Customer
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);

        // 2. Create Account and assign Customer
        Account account = new Account();
        account.setCustomer(customer);
        account.setBalance(0);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(accountType);

        // 3. Save Account (Customer saved automatically with CascadeType.ALL)
        service.openAccount(account);

        return "redirect:/account/list";
    }

    private String generateAccountNumber() {
        Random random = new Random();
        long number = 1000000000L + (long)(random.nextDouble() * 9000000000L);
        return String.valueOf(number);
    }



    // Show Transfer Form
    @GetMapping("/transfer")
    public String transferForm(Model model) {
        model.addAttribute("accounts", service.getAll()); // list of all accounts for dropdown
        return "transfer_form";
    }

    // Handle Transfer
    @PostMapping("/transfer")
    public String transfer(@RequestParam String fromAccNo,
                           @RequestParam String toAccNo,
                           @RequestParam double amount,
                           Model model) {
        try {
            service.transfer(fromAccNo, toAccNo, amount);
            model.addAttribute("message", "Transfer successful!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "transfer_form";
        }
        return "redirect:/account/list"; // or return "transfer_form" to show errors
    }

    @GetMapping("/list")
    public String listAccounts(
            @RequestParam(value = "search", required = false) String searchTerm,
            @RequestParam(value = "accountNumber", required = false) String accountNumber,
            Model model) {

        List<Account> accounts;

        // ✅ If account number is entered
        if (accountNumber != null && !accountNumber.trim().isEmpty()) {
            Account account = accountRepository.findByAccountNumber(accountNumber.trim());
            if (account != null) {
                accounts = List.of(account);
            } else {
                accounts = List.of();
                model.addAttribute("message", "❌ No account found for account number: " + accountNumber);
            }
        }
        // ✅ If customer name is entered
        else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            accounts = accountRepository.findByCustomer_NameContainingIgnoreCase(searchTerm.trim());
            if (accounts.isEmpty()) {
                model.addAttribute("message", "❌ No results found for name: " + searchTerm);
            }
        }
        // ✅ If nothing entered → show all
        else {
            accounts = accountRepository.findAll();
        }

        // ✅ Add both accounts and transactions for the same page
        model.addAttribute("accounts", accounts);
        model.addAttribute("transactions", service.getAllTransactions());

        // ✅ Keep search fields filled after search
        model.addAttribute("search", searchTerm);
        model.addAttribute("accountNumber", accountNumber);

        return "account_list";
    }
    @GetMapping("/transactions")
    public String showTransactions(Model model) {
        model.addAttribute("transactions", service.getAllTransactions());
        return "transaction_list"; // must match your HTML file name
    }

//    @GetMapping("/admin/dashboard")
//    public String adminDashboard(Model model) {
//        model.addAttribute("totalCustomers", customerRepo.count());
//        model.addAttribute("totalAccounts", customerRepo.count());
//        model.addAttribute("totalTransactions", transactionRepo.count());
//        model.addAttribute("recentTransactions", transactionRepo.findTop5ByOrderByDateTimeDesc());
//        return "admin_dashboard";
//    }


    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        long totalCustomers = customerRepo.count();
        long totalAccounts = accountRepository.count();
        long totalTransactions = transactionRepo.count();

        double totalMoney = accountRepository.findAll()
                .stream()
                .mapToDouble(Account::getBalance)
                .sum();

        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalAccounts", totalAccounts);
        model.addAttribute("totalTransactions", totalTransactions);
        model.addAttribute("totalMoney", totalMoney);

        return "admin_dashboard"; // ✅ must match the file name (not a redirect)
    }

    @GetMapping("/close/{id}")
    public String closeAccount(@PathVariable Long id) {
        service.closeAccount(id);
        return "redirect:/account/list"; // after closing, go back to account list
    }

}
