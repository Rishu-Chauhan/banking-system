//package com.bankingsystem.bankingsystem.config;
//
//import com.bankingsystem.bankingsystem.service.CustomUserDetailsService;
//import com.bankingsystem.bankingsystem.service.OtpService;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.*;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private CustomUserDetailsService userDetailsService;
//
//    // Authentication Manager
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//
//    // Password Encoder
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // Authentication Provider
//    @Bean
//    public DaoAuthenticationProvider authProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    // Custom Success Handler
//    @Bean
//    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
//        return (request, response, authentication) -> {
//            var authorities = authentication.getAuthorities();
//            String role = authorities.iterator().next().getAuthority();
//
//            if (role.equals("ROLE_ADMIN")) {
//                response.sendRedirect("/account/admin/dashboard");
//            } else if (role.equals("ROLE_CUSTOMER")) {
//                response.sendRedirect("/user/dashboard");
//            } else {
//                response.sendRedirect("/login?error=true");
//            }
//        };
//    }
//
//    // Security Filter Chain
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/css/**", "/js/**", "/register", "/login", "/h2-console/**","/test-email").permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/user/**").hasRole("CUSTOMER")
//                        .requestMatchers("/account/open", "/account/delete/**").hasRole("ADMIN")
//                        .requestMatchers("/account/**").hasAnyRole("ADMIN", "CUSTOMER")
//                        .anyRequest().authenticated()
//                )
//
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .successHandler(myAuthenticationSuccessHandler()) // ✅ Custom role-based redirect
//                        .permitAll()
//                )
//
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/login?logout=true")
//                        .permitAll()
//                )
//
//                .csrf(csrf -> csrf.disable()); // disable CSRF for now
//
//        return http.build();
//    }
//    @Component
//    public class CustomAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//        @Autowired
//        private OtpService otpService;
//
//        @Override
//        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                            Authentication authentication) throws IOException, ServletException {
//            String username = authentication.getName();
//            otpService.generateAndSendOtp(username);
//            response.sendRedirect("/verify-otp?user=" + username);
//        }
//    }
//
//}

package com.bankingsystem.bankingsystem.config;

import com.bankingsystem.bankingsystem.service.CustomUserDetailsService;
import com.bankingsystem.bankingsystem.service.OtpService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthSuccessHandler customAuthSuccessHandler; // ✅ Use OTP handler

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication Provider
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/register", "/login", "/h2-console/**", "/test-email", "/verify-otp", "/verify-otp/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("CUSTOMER")
                        .requestMatchers("/account/open", "/account/delete/**").hasRole("ADMIN")
                        .requestMatchers("/account/**").hasAnyRole("ADMIN", "CUSTOMER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthSuccessHandler) // ✅ Always go through OTP
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()); // disable CSRF for now

        return http.build();
    }

    // ✅ OTP-based Success Handler
    @Component
    public static class CustomAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        @Autowired
        private OtpService otpService;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws IOException, ServletException {
            String username = authentication.getName();
            System.out.println("✅ CustomAuthSuccessHandler triggered for user: " + username);

            otpService.generateAndSendOtp(username); // send OTP email
            response.sendRedirect("/verify-otp?user=" + username); // redirect to OTP page
        }
    }
}
