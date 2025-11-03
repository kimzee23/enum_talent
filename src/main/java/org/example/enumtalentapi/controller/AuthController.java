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

import java.util.HashMap;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String result = authService.login(request);

            // Parse the JSON string returned by authService
            Map<String, Object> loginData = parseLoginResult(result);

            // Create the exact response format frontend expects
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", loginData.get("token"));
            response.put("userId", loginData.get("userId"));
            response.put("email", loginData.get("email"));

            log.info("Login successful for user: {}", loginData.get("email"));
            return ResponseEntity.ok(response);

        } catch (CustomException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            log.warn("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Unexpected error during login");
            log.error("Unexpected login error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
                    log.info("Logout request for user: {}", userId);
                    return authService.logout(userId);
                },
                "Logout successful"
        );
    }

    @PostMapping("/logout-token")
    public ResponseEntity<ApiResponse> logoutWithToken(@RequestHeader("Authorization") String authHeader) {
        return handleRequest(
                () -> {
                    String token = authHeader.replace("Bearer ", "").trim();
                    return authService.logoutWithToken(token);
                },
                "Logout successful"
        );
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


    private Map<String, Object> parseLoginResult(String result) {
        try {
            // Remove curly braces and split by commas
            String cleanResult = result.replace("{", "").replace("}", "").replace("\"", "");
            String[] keyValuePairs = cleanResult.split(",");

            Map<String, Object> data = new HashMap<>();
            for (String pair : keyValuePairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    data.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }

            // Validate that required fields are present
            if (!data.containsKey("token") || !data.containsKey("userId")) {
                throw new CustomException("Invalid login response from service");
            }

            return data;

        } catch (Exception e) {
            log.error("Failed to parse login result: {}", result);
            throw new CustomException("Failed to process login response");
        }
    }

    @FunctionalInterface
    private interface ServiceAction {
        String execute() throws Exception;
    }
}