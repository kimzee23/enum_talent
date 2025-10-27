package org.example.enumtalentapi.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enumtalentapi.dto.LoginRequest;
import org.example.enumtalentapi.dto.SignupRequest;
import org.example.enumtalentapi.entity.User;
import org.example.enumtalentapi.entity.VerificationToken;
import org.example.enumtalentapi.exception.CustomException;
import org.example.enumtalentapi.repository.UserRepository;
import org.example.enumtalentapi.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final VerificationTokenRepository tokenRepo;
    private final PasswordEncoder encoder;
    private final EmailService emailService;

    @Override
    public String signup(SignupRequest request) {
        Optional<User> existingUser = userRepo.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.isVerified()) {
                throw new CustomException("Email already exstis ");
            } else {
                tokenRepo.deleteByUser(user);
                VerificationToken newToken = createVerificationToken(user);
                emailService.sendVerificationEmail(user.getEmail(), newToken.getToken());
                return "Verification email sent. Please check your inbox to verify your account.";
            }
        }
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(encoder.encode(request.getPassword()));
        newUser.setVerified(false);
        newUser.setCreatedAt(LocalDateTime.now());
        userRepo.save(newUser);

        VerificationToken token = createVerificationToken(newUser);
        emailService.sendVerificationEmail(newUser.getEmail(), token.getToken());

        return "Signup successful! Please check your email for verification link.";
    }

    private VerificationToken createVerificationToken(User user) {
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        return tokenRepo.save(token);
    }

    @Override
    public String login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("INVALID_CREDENTIALS"));

        if (!user.isVerified()) {
            throw new CustomException("EMAIL_NOT_VERIFIED");
        }

        boolean matches = encoder.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            throw new CustomException("INVALID_CREDENTIALS");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepo.save(user);
        VerificationToken newToken = createVerificationToken(user);
        return "LOGIN_SUCCESS - userId=" + user.getId() + " Token " + newToken.getToken();
    }

    @Override
    public String verifyEmail(String tokenStr) {
        VerificationToken token = tokenRepo.findByToken(tokenStr);
        if (token == null) {
            throw new CustomException("TOKEN_INVALID");
        }

        if (token.isUsed()) {
            throw new CustomException("TOKEN_ALREADY_USED");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException("TOKEN_EXPIRED");
        }

        User user = token.getUser();
        user.setVerified(true);
        token.setUsed(true);

        tokenRepo.save(token);
        userRepo.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getEmail().split("@")[0]);

        return "EMAIL_VERIFIED_SUCCESSFULLY";
    }

    @Override
    public String logout(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND"));

        user.setLastLogout(LocalDateTime.now());
        userRepo.save(user);

        log.info("User {} logged out successfully", user.getEmail());
        return "LOGOUT_SUCCESSFUL";
    }
    @Override
    public String logoutWithToken(String token) {
        log.info("Token-based logout requested");
        return "LOGOUT_SUCCESSFUL";
    }
}