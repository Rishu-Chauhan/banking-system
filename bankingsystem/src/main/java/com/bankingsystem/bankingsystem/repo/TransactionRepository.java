package com.bankingsystem.bankingsystem.repo;

import com.bankingsystem.bankingsystem.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Object findTop5ByOrderByDateTimeDesc();
}
