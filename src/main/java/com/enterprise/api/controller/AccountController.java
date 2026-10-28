package com.enterprise.api.controller;

import com.enterprise.api.dto.request.AccountCreateRequest;
import com.enterprise.api.dto.response.AccountResponse;
import com.enterprise.api.dto.response.ApiResponse;
import com.enterprise.api.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account Management", description = "Endpoints for managing financial accounts linked to customers")
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create financial account", description = "Creates a new financial account attached to an existing customer")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Account created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get account by ID", description = "Fetches account details with optimized customer JOIN FETCH query")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(@PathVariable Long id) {
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Account retrieved successfully"));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "List accounts by customer ID", description = "Retrieves all accounts owned by a given customer")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccountsByCustomerId(@PathVariable Long customerId) {
        List<AccountResponse> response = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer accounts retrieved successfully"));
    }
}
