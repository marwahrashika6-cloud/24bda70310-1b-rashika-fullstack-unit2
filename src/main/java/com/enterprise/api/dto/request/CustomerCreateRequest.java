package com.enterprise.api.dto.request;

import com.enterprise.api.entity.CustomerStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CustomerCreateRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 60, message = "First name must be between 2 and 60 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 60, message = "Last name must be between 2 and 60 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email format")
    private String email;

    @NotBlank(message = "Tax ID / SSN is required")
    @Pattern(
            regexp = "^\\d{3}-\\d{2}-\\d{4}$|^[A-Z0-9]{9,12}$",
            message = "Tax ID must be formatted as SSN (XXX-XX-XXXX) or alphanumeric identifier (9-12 chars)"
    )
    private String taxId;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Phone number must be a valid E.164 international format (e.g. +14155552671)"
    )
    private String phoneNumber;

    private CustomerStatus status = CustomerStatus.ACTIVE;

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Initial balance cannot be negative")
    private BigDecimal initialBalance;

    public CustomerCreateRequest() {
    }

    public CustomerCreateRequest(String firstName, String lastName, String email, String taxId, String phoneNumber, CustomerStatus status, BigDecimal initialBalance) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.taxId = taxId;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.initialBalance = initialBalance;
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

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
