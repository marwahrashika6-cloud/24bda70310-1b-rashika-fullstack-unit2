package com.enterprise.api.controller;

import com.enterprise.api.dto.request.LoginRequest;
import com.enterprise.api.dto.request.RegisterRequest;
import com.enterprise.api.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully authenticate seeded admin user and return JWT access token")
    void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin@enterprise.com", "Admin@12345");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isString())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.email").value("admin@enterprise.com"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request with validation errors when registering with weak password")
    void testRegisterWeakPasswordValidationFailure() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "John Weak", "john.weak@example.com", "weak", Set.of(Role.ROLE_USER)
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.validationErrors.password").exists());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when accessing protected customer API without JWT")
    void testProtectedApiRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
