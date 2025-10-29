package org.example.enumtalentapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enumtalentapi.dto.Response.ApiResponse;
import org.example.enumtalentapi.dto.Request.LoginRequest;
import org.example.enumtalentapi.dto.Request.SignupRequest;
import org.example.enumtalentapi.exception.CustomException;
import org.example.enumtalentapi.service.AuthServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthServiceImpl authService;
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody SignupRequest request) {
        return handleRequest(
                () -> {
                    log.info("Signup request for email: {}", request.getEmail());
                    return authService.signup(request);
                },
                "Signup successful"
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        return handleRequest(() -> authService.login(request), "Login successful");
    }
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        return handleRequest(() -> authService.verifyEmail(token), "Email verified successfully");
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam("token") String token) {
        return handleRequest(() -> authService.verifyEmail(token), "Email verified successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @RequestParam String userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return handleRequest(
                () -> {
                    log.info("Logout request for user: {}", userId);return authService.logout(userId);}, "Logout successful");
    }

    @PostMapping("/logout-token")
    public ResponseEntity<ApiResponse> logoutWithToken(@RequestHeader("Authorization") String authHeader) {
        return handleRequest(
                () -> {
                    String token = authHeader.replace("Bearer ", "").trim();return authService.logoutWithToken(token);}, "Logout successful");
    }
    private ResponseEntity<ApiResponse> handleRequest(ServiceAction action, String successMessage) {
        try {
            String result = action.execute();
            return ResponseEntity.ok(new ApiResponse("success", result != null ? result : successMessage));
        } catch (CustomException e) {
            log.warn("Business error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @FunctionalInterface
    private interface ServiceAction {
        String execute() throws Exception;
    }
}
