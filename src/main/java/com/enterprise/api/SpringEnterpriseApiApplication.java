package com.enterprise.api;

import com.enterprise.api.entity.*;
import com.enterprise.api.repository.CustomerRepository;
import com.enterprise.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootApplication
public class SpringEnterpriseApiApplication {

    private static final Logger log = LoggerFactory.getLogger(SpringEnterpriseApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringEnterpriseApiApplication.class, args);
    }

    /**
     * Seeds initial demo users and sample customers for rapid local testing and portfolio evaluation.
     */
    @Bean
    public CommandLineRunner dataInitializer(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Bootstrapping demonstration users with BCrypt password hashing...");

                User admin = new User(
                        "Administrator User",
                        "admin@enterprise.com",
                        passwordEncoder.encode("Admin@12345"),
                        Set.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_USER)
                );

                User manager = new User(
                        "Operations Manager",
                        "manager@enterprise.com",
                        passwordEncoder.encode("Manager@12345"),
                        Set.of(Role.ROLE_MANAGER, Role.ROLE_USER)
                );

                User standardUser = new User(
                        "Standard User",
                        "user@enterprise.com",
                        passwordEncoder.encode("User@12345"),
                        Set.of(Role.ROLE_USER)
                );

                userRepository.save(admin);
                userRepository.save(manager);
                userRepository.save(standardUser);
                log.info("Users seeded: admin@enterprise.com, manager@enterprise.com, user@enterprise.com");
            }

            if (customerRepository.count() == 0) {
                log.info("Bootstrapping sample customer records with transparent AES-256-GCM field encryption...");

                Customer c1 = new Customer(
                        "Alex", "Mercer", "alex.mercer@example.com",
                        "987-65-4321", "+14155550101",
                        CustomerStatus.ACTIVE, new BigDecimal("12500.50")
                );
                c1.addAccount(new Account("ACCT-100101", "CHECKING", new BigDecimal("7500.50"), "USD", "ACTIVE", c1));
                c1.addAccount(new Account("ACCT-100102", "SAVINGS", new BigDecimal("5000.00"), "USD", "ACTIVE", c1));

                Customer c2 = new Customer(
                        "Elena", "Rostova", "elena.rostova@example.com",
                        "876-54-3210", "+14155550102",
                        CustomerStatus.ACTIVE, new BigDecimal("48200.00")
                );
                c2.addAccount(new Account("ACCT-200201", "INVESTMENT", new BigDecimal("48200.00"), "USD", "ACTIVE", c2));

                Customer c3 = new Customer(
                        "Marcus", "Vance", "marcus.vance@example.com",
                        "765-43-2109", "+14155550103",
                        CustomerStatus.PENDING_VERIFICATION, new BigDecimal("1200.00")
                );
                c3.addAccount(new Account("ACCT-300301", "CHECKING", new BigDecimal("1200.00"), "USD", "ACTIVE", c3));

                customerRepository.save(c1);
                customerRepository.save(c2);
                customerRepository.save(c3);
                log.info("Sample customers seeded with encrypted Tax IDs and Phone numbers.");
            }
        };
    }
}
