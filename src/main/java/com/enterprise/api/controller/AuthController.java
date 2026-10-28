package com.enterprise.api.controller;

import com.enterprise.api.dto.request.LoginRequest;
import com.enterprise.api.dto.request.RefreshTokenRequest;
import com.enterprise.api.dto.request.RegisterRequest;
import com.enterprise.api.dto.response.ApiResponse;
import com.enterprise.api.dto.response.AuthResponse;
import com.enterprise.api.dto.response.UserProfileResponse;
import com.enterprise.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication & Authorization", description = "Endpoints for user registration, JWT login, and token refresh")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with hashed password and role assignment")
    public ResponseEntity<ApiResponse<UserProfileResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserProfileResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns signed JWT access & refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Authentication successful"));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Issues a new JWT access token using an unexpired refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated profile", description = "Fetches the profile details of the authenticated caller")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser() {
        UserProfileResponse response = authService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success(response, "Current user profile retrieved successfully"));
    }
}
