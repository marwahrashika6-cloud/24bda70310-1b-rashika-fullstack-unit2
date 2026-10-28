package com.enterprise.api.entity;

import com.enterprise.api.util.encryption.AesGcmAttributeConverter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_email", columnList = "email", unique = true),
        @Index(name = "idx_customer_status", columnList = "status"),
        @Index(name = "idx_customer_last_name", columnList = "last_name"),
        @Index(name = "idx_customer_created_at", columnList = "created_at")
})
public class Customer extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Highly sensitive Tax Identification / SSN encrypted at rest using AES-256-GCM.
     */
    @Convert(converter = AesGcmAttributeConverter.class)
    @Column(name = "tax_id", nullable = false, length = 512)
    private String taxId;

    /**
     * Highly sensitive contact information encrypted at rest using AES-256-GCM.
     */
    @Convert(converter = AesGcmAttributeConverter.class)
    @Column(name = "phone_number", nullable = false, length = 512)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CustomerStatus status = CustomerStatus.ACTIVE;

    @Column(name = "total_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalBalance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    public Customer() {
    }

    public Customer(String firstName, String lastName, String email, String taxId, String phoneNumber, CustomerStatus status, BigDecimal totalBalance) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.taxId = taxId;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.totalBalance = totalBalance;
    }

    public void addAccount(Account account) {
        accounts.add(account);
        account.setCustomer(this);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setCustomer(null);
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
