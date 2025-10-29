package com.bankingsystem.bankingsystem.service;

import com.bankingsystem.bankingsystem.model.User;
import com.bankingsystem.bankingsystem.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // ⚠ Remove "ROLE_" prefix here
        String cleanRole = user.getRole().replace("ROLE_", "");
        System.out.println("✅ Loaded user: " + user.getUsername() + " with role: " + cleanRole);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(cleanRole) // Spring adds ROLE_ automatically
                .build();

    }

}
