package com.enterprise.api.dto.response;

import java.time.Instant;
import java.util.Set;

public class UserProfileResponse {

    private Long id;
    private String fullName;
    private String email;
    private Set<String> roles;
    private boolean enabled;
    private Instant createdAt;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String fullName, String email, Set<String> roles, boolean enabled, Instant createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
