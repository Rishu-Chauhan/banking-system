package com.bankingsystem.bankingsystem.repo;


import com.bankingsystem.bankingsystem.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Account findByAccountNumber(String accountNumber);
    List<Account> findByCustomer_NameContainingIgnoreCase(String name);

    @Query("SELECT SUM(a.balance) FROM Account a")
    Double sumAllBalances();



}
