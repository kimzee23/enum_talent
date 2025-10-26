package org.example.enumtalentapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.enumtalentapi.controller.AuthController;
import org.example.enumtalentapi.dto.LoginRequest;
import org.example.enumtalentapi.dto.SignupRequest;
import org.example.enumtalentapi.exception.CustomException;
import org.example.enumtalentapi.service.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthServiceImpl authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void signup_Success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("bovi@gmail.com");
        request.setPassword("password123");

        when(authService.signup(any(SignupRequest.class))).thenReturn("Signup successful. Please verify your email.");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Signup successful. Please verify your email."));

        verify(authService, times(1)).signup(any(SignupRequest.class));
    }

    @Test
    void signup_CustomException() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("bovi@gmail.com");
        request.setPassword("password123");

        when(authService.signup(any(SignupRequest.class)))
                .thenThrow(new CustomException("EMAIL_IN_USE"));
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("EMAIL_IN_USE"));

        verify(authService, times(1)).signup(any(SignupRequest.class));
    }

    @Test
    void signup_InternalServerError() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("bovi@gmail.com");
        request.setPassword("password123");

        when(authService.signup(any(SignupRequest.class)))
                .thenThrow(new RuntimeException("Database error"));
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Unexpected error: Database error"));

        verify(authService, times(1)).signup(any(SignupRequest.class));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("bovi@gmail.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class))).thenReturn("LOGIN_SUCCESS - userId=123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("LOGIN_SUCCESS - userId=123"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new CustomException("INVALID_CREDENTIALS"));
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("INVALID_CREDENTIALS"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void verifyEmail_Get_Success() throws Exception {
        String token = "valid-token-123";
        when(authService.verifyEmail(token)).thenReturn("EMAIL_VERIFIED_SUCCESSFULLY");
        mockMvc.perform(get("/api/auth/verify")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("EMAIL_VERIFIED_SUCCESSFULLY"));

        verify(authService, times(1)).verifyEmail(token);
    }

    @Test
    void verifyEmail_Post_Success() throws Exception {
        String token = "valid-token-123";
        Map<String, String> requestBody = Map.of("token", token);

        when(authService.verifyEmail(token)).thenReturn("EMAIL_VERIFIED_SUCCESSFULLY");
        mockMvc.perform(post("/api/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("EMAIL_VERIFIED_SUCCESSFULLY"));

        verify(authService, times(1)).verifyEmail(token);
    }

    @Test
    void verifyEmail_InvalidToken() throws Exception {
        String token = "invalid-token";
        when(authService.verifyEmail(token))
                .thenThrow(new CustomException("TOKEN_INVALID"));
        mockMvc.perform(get("/api/auth/verify")
                        .param("token", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("TOKEN_INVALID"));

        verify(authService, times(1)).verifyEmail(token);
    }

    @Test
    void verifyEmail_ExpiredToken() throws Exception {
        String token = "expired-token";
        when(authService.verifyEmail(token))
                .thenThrow(new CustomException("TOKEN_EXPIRED"));
        mockMvc.perform(get("/api/auth/verify")
                        .param("token", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("TOKEN_EXPIRED"));

        verify(authService, times(1)).verifyEmail(token);
    }
}