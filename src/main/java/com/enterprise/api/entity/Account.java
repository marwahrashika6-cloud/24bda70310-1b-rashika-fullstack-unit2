package com.enterprise.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts", indexes = {
        @Index(name = "idx_account_number", columnList = "account_number", unique = true),
        @Index(name = "idx_account_customer_id", columnList = "customer_id"),
        @Index(name = "idx_account_status", columnList = "status")
})
public class Account extends BaseEntity {

    @Column(name = "account_number", nullable = false, unique = true, length = 32)
    private String accountNumber;

    @Column(name = "account_type", nullable = false, length = 30)
    private String accountType; // CHECKING, SAVINGS, INVESTMENT

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    public Account() {
    }

    public Account(String accountNumber, String accountType, BigDecimal balance, String currency, String status, Customer customer) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
        this.customer = customer;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
