package org.example.enumtalentapi;

import org.example.enumtalentapi.controller.AuthController;
import org.example.enumtalentapi.exception.CustomException;
import org.example.enumtalentapi.service.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerLogoutTest {

    private MockMvc mockMvc;

    @Mock
    private AuthServiceImpl authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void logout_Success() throws Exception {
        String userId = "68fb32738623940fa372fb2e";
        when(authService.logout(userId)).thenReturn("LOGOUT_SUCCESSFUL");

        mockMvc.perform(post("/api/auth/logout")
                        .param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("LOGOUT_SUCCESSFUL"));

        verify(authService, times(1)).logout(userId);
    }

    @Test
    void logout_UserNotFound() throws Exception {
        String userId = "non-existent-user";
        when(authService.logout(userId))
                .thenThrow(new CustomException("USER_NOT_FOUND"));

        mockMvc.perform(post("/api/auth/logout")
                        .param("userId", userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("USER_NOT_FOUND"));

        verify(authService, times(1)).logout(userId);
    }

    @Test
    void logoutWithToken_Success() throws Exception {
        String token = "Bearer valid-jwt-token";
        when(authService.logoutWithToken(anyString())).thenReturn("LOGOUT_SUCCESSFUL");

        mockMvc.perform(post("/api/auth/logout-token")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("LOGOUT_SUCCESSFUL"));

        verify(authService, times(1)).logoutWithToken("valid-jwt-token");
    }

    @Test
    void logout_InternalServerError() throws Exception {
        String userId = "68fb32738623940fa372fb2e";
        when(authService.logout(userId))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/auth/logout")
                        .param("userId", userId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Unexpected error: Database error"));

        verify(authService, times(1)).logout(userId);
    }
}