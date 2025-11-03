package org.example.enumtalentapi.dto.Response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String userId;
    private String email;
    private String message;
}