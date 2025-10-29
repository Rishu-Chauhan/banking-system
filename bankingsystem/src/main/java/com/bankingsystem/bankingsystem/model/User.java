package com.bankingsystem.bankingsystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role; // ROLE_ADMIN or ROLE_CUSTOMER
    private String email;

    private String otp;
    private LocalDateTime otpGeneratedTime;
}
