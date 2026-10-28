package com.enterprise.api.service;

import com.enterprise.api.config.CacheConfig;
import com.enterprise.api.dto.request.CustomerCreateRequest;
import com.enterprise.api.dto.request.CustomerSearchCriteria;
import com.enterprise.api.dto.request.CustomerUpdateRequest;
import com.enterprise.api.dto.response.AccountResponse;
import com.enterprise.api.dto.response.CustomerResponse;
import com.enterprise.api.dto.response.PagedResponse;
import com.enterprise.api.entity.Account;
import com.enterprise.api.entity.Customer;
import com.enterprise.api.exception.DuplicateResourceException;
import com.enterprise.api.exception.ResourceNotFoundException;
import com.enterprise.api.repository.CustomerRepository;
import com.enterprise.api.specification.CustomerSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Persists new customer. Tax ID and Phone Number are encrypted transparently
     * at rest via JPA AesGcmAttributeConverter before hitting the database table.
     */
    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Customer already exists with email: " + request.getEmail());
        }

        Customer customer = new Customer(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getTaxId(),
                request.getPhoneNumber(),
                request.getStatus(),
                request.getInitialBalance()
        );

        // Optionally create a primary default checking account
        Account defaultAccount = new Account(
                "ACCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "CHECKING",
                request.getInitialBalance(),
                "USD",
                "ACTIVE",
                customer
        );
        customer.addAccount(defaultAccount);

        Customer saved = customerRepository.save(customer);
        log.info("Successfully created customer record with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    /**
     * Read-optimized customer lookup with in-memory Caffeine caching.
     * Subsequent queries with the same customer ID skip database execution completely.
     * Prevents N+1 queries via Repository @EntityGraph.
     */
    @Cacheable(value = CacheConfig.CUSTOMERS_CACHE, key = "#id")
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        log.info("Cache miss for customer ID: {}. Executing optimized database fetch with JOIN FETCH.", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return mapToResponse(customer);
    }

    /**
     * Highly scalable paginated and sortable query with dynamic specifications.
     */
    @Transactional(readOnly = true)
    public PagedResponse<CustomerResponse> searchCustomers(CustomerSearchCriteria criteria, Pageable pageable) {
        Specification<Customer> spec = CustomerSpecification.buildSpecification(criteria);
        Page<Customer> page = customerRepository.findAll(spec, pageable);
        return PagedResponse.of(page, this::mapToResponse);
    }

    /**
     * Updates customer record and automatically invalidates the cache entry.
     */
    @CacheEvict(value = CacheConfig.CUSTOMERS_CACHE, key = "#id")
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            customer.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getStatus() != null) {
            customer.setStatus(request.getStatus());
        }

        Customer updated = customerRepository.save(customer);
        log.info("Customer id {} updated and cache evicted", id);
        return mapToResponse(updated);
    }

    /**
     * Deletes customer and invalidates cache entry.
     */
    @CacheEvict(value = CacheConfig.CUSTOMERS_CACHE, key = "#id")
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer", "id", id);
        }
        customerRepository.deleteById(id);
        log.info("Customer id {} deleted and cache evicted", id);
    }

    public CustomerResponse mapToResponse(Customer customer) {
        List<AccountResponse> accountResponses = customer.getAccounts() == null ? Collections.emptyList() :
                customer.getAccounts().stream()
                        .map(account -> new AccountResponse(
                                account.getId(),
                                account.getAccountNumber(),
                                account.getAccountType(),
                                account.getBalance(),
                                account.getCurrency(),
                                account.getStatus(),
                                customer.getId(),
                                account.getCreatedAt()
                        ))
                        .toList();

        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                maskSensitive(customer.getTaxId(), 4),
                maskSensitive(customer.getPhoneNumber(), 4),
                customer.getStatus(),
                customer.getTotalBalance(),
                accountResponses,
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    private String maskSensitive(String value, int visibleSuffixChars) {
        if (value == null || value.length() <= visibleSuffixChars) {
            return "****";
        }
        int maskedLength = value.length() - visibleSuffixChars;
        return "*".repeat(maskedLength) + value.substring(maskedLength);
    }
}
