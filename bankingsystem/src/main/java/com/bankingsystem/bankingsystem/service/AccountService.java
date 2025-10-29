package com.bankingsystem.bankingsystem.service;

import com.bankingsystem.bankingsystem.model.Account;
import com.bankingsystem.bankingsystem.model.Transaction;
import com.bankingsystem.bankingsystem.repo.AccountRepository;
import com.bankingsystem.bankingsystem.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repo;

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private EmailService emailService;

    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    public Account getAccountByAccountNumber(String accountNumber) {
        return repo.findByAccountNumber(accountNumber);
    }

    public List<Account> getAll() {
        return repo.findAll();
    }

    public Account openAccount(Account account) {
        account.setBalance(0);
        return repo.save(account);
    }


    public void deposit(String accNo, double amount) {
        Account acc = repo.findByAccountNumber(accNo);
        if (acc == null) throw new RuntimeException("Account not found");
        if ("CLOSED".equals(acc.getStatus())) {
            throw new RuntimeException("This account is closed. No transactions allowed.");
        }
        acc.setBalance(acc.getBalance() + amount);
        repo.save(acc);

        // ✅ Send Email
        String subject = "Deposit Successful";
        String body = "Dear " + acc.getCustomer().getName() + ",\n\n" +
                "Your deposit of ₹" + amount + " was successful.\n" +
                "Current Balance: ₹" + acc.getBalance() + "\n\n" +
                "Thank you for banking with us!\n- Banking System Team";

        emailService.sendEmail(acc.getCustomer().getEmail(), subject, body);



        // Save transaction
        Transaction t = new Transaction();
        t.setAccount(acc);
        t.setAmount(amount);
        t.setType("Deposit");
        t.setDateTime(LocalDateTime.now());
        t.setDetails("Deposit to account");
        transactionRepo.save(t);
    }

    public void withdraw(String accNo, double amount) {
        Account acc = repo.findByAccountNumber(accNo);
        if (acc == null) throw new RuntimeException("Account not found");
        if ("CLOSED".equals(acc.getStatus())) {
            throw new RuntimeException("This account is closed. No transactions allowed.");
        }
        double minBalance = 500.0;
        // Check if withdrawal breaks the minimum balance rule
        if (acc.getBalance() - amount < minBalance) {
            throw new RuntimeException("❌ Minimum balance of ₹500 must be maintained.");
        }

        if (acc.getBalance() < amount) throw new RuntimeException("Insufficient balance");
        acc.setBalance(acc.getBalance() - amount);
        repo.save(acc);



        // ✅ Send Email Notification (Withdrawal)
        String subject = "Withdrawal Alert - ₹" + amount;
        String body = "Dear " + acc.getCustomer().getName() + ",\n\n" +
                "An amount of ₹" + amount + " has been withdrawn from your account.\n" +
                "Account Number: " + acc.getAccountNumber() + "\n" +
                "Available Balance: ₹" + acc.getBalance() + "\n\n" +
                "If you did not make this transaction, please contact our support immediately.\n\n" +
                "Thank you for banking with us!";

        emailService.sendEmail(acc.getCustomer().getEmail(), subject, body);



        // Save transaction
        Transaction t = new Transaction();
        t.setAccount(acc);
        t.setAmount(amount);
        t.setType("Withdraw");
        t.setDateTime(LocalDateTime.now());
        t.setDetails("Withdrawal from account");
        transactionRepo.save(t);
    }

    public void transfer(String fromAccNo, String toAccNo, double amount) {
        Account sender = repo.findByAccountNumber(fromAccNo);
        Account receiver = repo.findByAccountNumber(toAccNo);

        if (sender == null || receiver == null) throw new RuntimeException("Account not found");
        if ("CLOSED".equals(sender.getStatus())) {
            throw new RuntimeException("This account is closed. No transactions allowed.");
        }
        if ("CLOSED".equals(receiver.getStatus())) {
            throw new RuntimeException("This account is closed. No transactions allowed.");
        }
        double minBalance = 500.0;
        // Check if sender maintains minimum balance
        if (sender.getBalance() - amount < minBalance) {
            throw new RuntimeException("❌ Sender must maintain a minimum balance of ₹500.");
        }
        if (sender.getBalance() < amount) throw new RuntimeException("Insufficient balance");



        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);
        repo.save(sender);
        repo.save(receiver);

        // Save transactions
        Transaction t1 = new Transaction();
        t1.setAccount(sender);
        t1.setAmount(amount);
        t1.setType("Transfer");
        t1.setDateTime(LocalDateTime.now());
        t1.setDetails("Transferred to " + receiver.getAccountNumber());
        transactionRepo.save(t1);

        Transaction t2 = new Transaction();
        t2.setAccount(receiver);
        t2.setAmount(amount);
        t2.setType("Transfer");
        t2.setDateTime(LocalDateTime.now());
        t2.setDetails("Received from " + sender.getAccountNumber());
        transactionRepo.save(t2);

        // ✅ Send email notification to sender
        String subject = "Transaction Alert - ₹" + amount;
        String body = "Dear " + sender.getCustomer().getName() + ",\n\n" +
                "Your account has been debited by ₹" + amount + ".\n" +
                "Transferred To: " + receiver.getCustomer().getName() + "\n" +
                "Available Balance: ₹" + sender.getBalance() + "\n\n" +
                "Thank you for banking with us!";

        emailService.sendEmail(sender.getCustomer().getEmail(), subject, body);

// ✅ (Optional) Send email to receiver too
        String subject2 = "You’ve received ₹" + amount;
        String body2 = "Dear " + receiver.getCustomer().getName() + ",\n\n" +
                "Your account has been credited with ₹" + amount + ".\n" +
                "Received from: " + sender.getCustomer().getName() + "\n" +
                "Your new balance is ₹" + receiver.getBalance() + "\n\n" +
                "Thank you for banking with us!";

        emailService.sendEmail(receiver.getCustomer().getEmail(), subject2, body2);
    }

    public void closeAccount(Long id) {
        Account account = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus("CLOSED");
        repo.save(account);
    }

    public List<Account> getAllAccounts() {
        return repo.findAll();
    }
}
