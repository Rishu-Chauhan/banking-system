package com.bankingsystem.bankingsystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;


    private double balance;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE or CLOSED

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String accountType; // SAVINGS, CURRENT, FIXED_DEPOSIT
}
