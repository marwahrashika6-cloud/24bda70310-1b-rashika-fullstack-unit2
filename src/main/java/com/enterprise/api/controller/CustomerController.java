package com.enterprise.api.controller;

import com.enterprise.api.dto.request.CustomerCreateRequest;
import com.enterprise.api.dto.request.CustomerSearchCriteria;
import com.enterprise.api.dto.request.CustomerUpdateRequest;
import com.enterprise.api.dto.response.ApiResponse;
import com.enterprise.api.dto.response.CustomerResponse;
import com.enterprise.api.dto.response.PagedResponse;
import com.enterprise.api.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Management", description = "REST APIs for encrypted customer management, read-optimized caching, and paginated search")
@SecurityRequirement(name = "Bearer Authentication")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create customer record", description = "Persists customer with AES-256-GCM encryption for Tax ID & phone number")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerCreateRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Customer created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get customer by ID", description = "Read-optimized fetch backed by in-memory Caffeine cache and N+1 query elimination")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable Long id) {
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(
            summary = "Paginated & Sortable Customer Search",
            description = "Dynamically queries customers matching criteria with custom pagination and multi-field sorting"
    )
    public ResponseEntity<ApiResponse<PagedResponse<CustomerResponse>>> getCustomers(
            @ModelAttribute CustomerSearchCriteria criteria,
            @Parameter(description = "Pagination parameters (e.g. page=0&size=10&sort=createdAt,desc)")
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedResponse<CustomerResponse> response = customerService.searchCustomers(criteria, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Customers retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update customer record", description = "Updates customer metadata and invalidates the cached representation")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request) {
        CustomerResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete customer record", description = "Deletes customer and purges cache entries. Requires ROLE_ADMIN.")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.noContent("Customer deleted successfully"));
    }
}
