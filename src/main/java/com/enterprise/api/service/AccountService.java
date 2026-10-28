package com.enterprise.api.service;

import com.enterprise.api.config.CacheConfig;
import com.enterprise.api.dto.request.AccountCreateRequest;
import com.enterprise.api.dto.response.AccountResponse;
import com.enterprise.api.entity.Account;
import com.enterprise.api.entity.Customer;
import com.enterprise.api.exception.DuplicateResourceException;
import com.enterprise.api.exception.ResourceNotFoundException;
import com.enterprise.api.repository.AccountRepository;
import com.enterprise.api.repository.CustomerRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @CacheEvict(value = CacheConfig.CUSTOMERS_CACHE, key = "#request.customerId")
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        String generatedAccNumber = "ACCT-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        if (accountRepository.existsByAccountNumber(generatedAccNumber)) {
            throw new DuplicateResourceException("Account number collision detected. Please retry.");
        }

        Account account = new Account(
                generatedAccNumber,
                request.getAccountType(),
                request.getInitialDeposit(),
                request.getCurrency(),
                "ACTIVE",
                customer
        );

        customer.setTotalBalance(customer.getTotalBalance().add(request.getInitialDeposit()));
        Account saved = accountRepository.save(account);

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {
        Account account = accountRepository.findByIdWithCustomer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        return mapToResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }
        return accountRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AccountResponse mapToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency(),
                account.getStatus(),
                account.getCustomer().getId(),
                account.getCreatedAt()
        );
    }
}
