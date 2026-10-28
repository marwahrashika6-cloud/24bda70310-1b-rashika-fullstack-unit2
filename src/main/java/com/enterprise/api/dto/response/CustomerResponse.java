package com.enterprise.api.dto.response;

import com.enterprise.api.entity.CustomerStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class CustomerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String maskedTaxId;
    private String maskedPhoneNumber;
    private CustomerStatus status;
    private BigDecimal totalBalance;
    private List<AccountResponse> accounts;
    private Instant createdAt;
    private Instant updatedAt;

    public CustomerResponse() {
    }

    public CustomerResponse(Long id, String firstName, String lastName, String email,
                            String maskedTaxId, String maskedPhoneNumber,
                            CustomerStatus status, BigDecimal totalBalance,
                            List<AccountResponse> accounts, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.maskedTaxId = maskedTaxId;
        this.maskedPhoneNumber = maskedPhoneNumber;
        this.status = status;
        this.totalBalance = totalBalance;
        this.accounts = accounts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getMaskedTaxId() {
        return maskedTaxId;
    }

    public void setMaskedTaxId(String maskedTaxId) {
        this.maskedTaxId = maskedTaxId;
    }

    public String getMaskedPhoneNumber() {
        return maskedPhoneNumber;
    }

    public void setMaskedPhoneNumber(String maskedPhoneNumber) {
        this.maskedPhoneNumber = maskedPhoneNumber;
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

    public List<AccountResponse> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountResponse> accounts) {
        this.accounts = accounts;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
