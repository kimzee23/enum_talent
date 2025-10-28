package org.example.enumtalentapi.service;

import org.example.enumtalentapi.dto.Request.LoginRequest;
import org.example.enumtalentapi.dto.Request.SignupRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String signup(SignupRequest request);
    String login(LoginRequest request);
    String verifyEmail(String tokenStr);

    String logout(String userId);

    String logoutWithToken(String token);
}
