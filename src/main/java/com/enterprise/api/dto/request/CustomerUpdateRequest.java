package com.enterprise.api.dto.request;

import com.enterprise.api.entity.CustomerStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerUpdateRequest {

    @Size(min = 2, max = 60, message = "First name must be between 2 and 60 characters")
    private String firstName;

    @Size(min = 2, max = 60, message = "Last name must be between 2 and 60 characters")
    private String lastName;

    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Phone number must be a valid E.164 international format (e.g. +14155552671)"
    )
    private String phoneNumber;

    private CustomerStatus status;

    public CustomerUpdateRequest() {
    }

    public CustomerUpdateRequest(String firstName, String lastName, String phoneNumber, CustomerStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.status = status;
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
}
