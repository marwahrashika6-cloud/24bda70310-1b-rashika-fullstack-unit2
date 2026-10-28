package com.enterprise.api.dto.request;

import com.enterprise.api.entity.CustomerStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Filter criteria DTO for paginated and dynamic search queries.
 */
public class CustomerSearchCriteria {

    private String keyword;
    private CustomerStatus status;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAfter;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdBefore;

    public CustomerSearchCriteria() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public BigDecimal getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(BigDecimal minBalance) {
        this.minBalance = minBalance;
    }

    public BigDecimal getMaxBalance() {
        return maxBalance;
    }

    public void setMaxBalance(BigDecimal maxBalance) {
        this.maxBalance = maxBalance;
    }

    public Instant getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(Instant createdAfter) {
        this.createdAfter = createdAfter;
    }

    public Instant getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(Instant createdBefore) {
        this.createdBefore = createdBefore;
    }
}
