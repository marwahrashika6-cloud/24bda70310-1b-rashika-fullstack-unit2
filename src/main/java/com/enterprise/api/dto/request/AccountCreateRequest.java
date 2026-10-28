package com.enterprise.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public class AccountCreateRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "CHECKING|SAVINGS|INVESTMENT", message = "Account type must be CHECKING, SAVINGS, or INVESTMENT")
    private String accountType;

    @NotBlank(message = "Currency code is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code (e.g. USD, EUR, GBP)")
    private String currency;

    @NotNull(message = "Initial deposit amount is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Initial deposit cannot be negative")
    private BigDecimal initialDeposit;

    public AccountCreateRequest() {
    }

    public AccountCreateRequest(Long customerId, String accountType, String currency, BigDecimal initialDeposit) {
        this.customerId = customerId;
        this.accountType = accountType;
        this.currency = currency;
        this.initialDeposit = initialDeposit;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getInitialDeposit() {
        return initialDeposit;
    }

    public void setInitialDeposit(BigDecimal initialDeposit) {
        this.initialDeposit = initialDeposit;
    }
}
