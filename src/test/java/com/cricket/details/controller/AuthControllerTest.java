package com.cricket.details.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;

import com.cricket.details.model.AuthRequest;
import com.cricket.details.model.AuthResponse;
import com.cricket.details.service.AuthService;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void registerUser_ShouldReturnCreatedWithToken() throws Exception {
        AuthResponse mockAuthResponse = new AuthResponse("mock-jwt-token");
        Mockito.when(authService.register(any(AuthRequest.class))).thenReturn(mockAuthResponse);
        mockMvc.perform(post("/api/auth/register")
                .with(anonymous())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "username": "testuser",
                        "password": "testpass"
                        }
                        """)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));

    }

    @Test
    void securedEndpoint_ShouldRejectAnonymousAccess() throws Exception {
        mockMvc.perform(post("/api/secure/data")
                .with(anonymous())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // or Forbidden, depending on config
    }

    @Test
    void securedEndpoint_ShouldAllowAccessForAuthenticatedUser() throws Exception {
        mockMvc.perform(post("/api/secure/data")
                .with(user("testuser").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void securedEndpoint_ShouldRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/secure/endpoint"))
                .andExpect(status().isUnauthorized()); // or Forbidden depending on config
    }

}
