package com.enterprise.api.service;

import com.enterprise.api.dto.request.CustomerCreateRequest;
import com.enterprise.api.dto.response.CustomerResponse;
import com.enterprise.api.entity.Customer;
import com.enterprise.api.entity.CustomerStatus;
import com.enterprise.api.exception.DuplicateResourceException;
import com.enterprise.api.exception.ResourceNotFoundException;
import com.enterprise.api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer(
                "Jane", "Doe", "jane.doe@example.com",
                "123-45-6789", "+14155551234",
                CustomerStatus.ACTIVE, new BigDecimal("5000.00")
        );
        testCustomer.setId(1L);
    }

    @Test
    @DisplayName("Should successfully create a new customer and return response with masked sensitive fields")
    void testCreateCustomerSuccess() {
        CustomerCreateRequest request = new CustomerCreateRequest(
                "Jane", "Doe", "jane.doe@example.com",
                "123-45-6789", "+14155551234",
                CustomerStatus.ACTIVE, new BigDecimal("5000.00")
        );

        when(customerRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        assertEquals("jane.doe@example.com", response.getEmail());
        assertTrue(response.getMaskedTaxId().startsWith("****"));
        assertTrue(response.getMaskedPhoneNumber().startsWith("****"));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating customer with existing email")
    void testCreateCustomerDuplicateEmail() {
        CustomerCreateRequest request = new CustomerCreateRequest(
                "Jane", "Doe", "jane.doe@example.com",
                "123-45-6789", "+14155551234",
                CustomerStatus.ACTIVE, new BigDecimal("5000.00")
        );

        when(customerRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.createCustomer(request));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should successfully retrieve customer by ID")
    void testGetCustomerByIdSuccess() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        CustomerResponse response = customerService.getCustomerById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Jane", response.getFirstName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when customer ID is not found")
    void testGetCustomerByIdNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(999L));
    }
}
