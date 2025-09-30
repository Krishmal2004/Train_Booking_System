package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.User;
import com.example.train_systen.stationManager.dto.LoginRequest;
import com.example.train_systen.stationManager.dto.SignupRequest;
import com.example.train_systen.stationManager.service.AuthService;
import com.example.train_systen.stationManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    @Autowired
    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User authenticateUser(LoginRequest loginRequest) throws Exception {
        // Find user by username or email
        Optional<User> userOpt = userService.findByUsernameOrEmail(loginRequest.getUsername());

        if (userOpt.isEmpty()) {
            throw new Exception("User not found");
        }

        User user = userOpt.get();

        // Check if account is locked or disabled
        if (user.isAccountLocked()) {
            throw new Exception("Your account has been locked. Please contact support.");
        }

        if (!user.isAccountEnabled()) {
            throw new Exception("Your account is disabled. Please contact support.");
        }

        // Verify password (using direct comparison since we're not encoding passwords)
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new Exception("Invalid password");
        }

        // Update last login time
        userService.updateLastLogin(user);

        return user;
    }

    @Override
    public User registerUser(SignupRequest signupRequest, BindingResult result) {
        // Validate if username exists
        if (userService.existsByUsername(signupRequest.getUsername())) {
            result.rejectValue("username", "error.username", "Username is already taken");
        }

        // Validate if email exists
        if (userService.existsByEmail(signupRequest.getEmail())) {
            result.rejectValue("email", "error.email", "Email is already in use");
        }

        // Validate password match
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        // Check if validation failed
        if (result.hasErrors()) {
            return null;
        }

        // Create new user account
        return userService.registerNewUser(signupRequest);
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        // Implementation for password reset token validation
        // Would typically query a password_reset_tokens table
        return false; // Placeholder
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        // Implementation for creating password reset token
        // Would typically save to a password_reset_tokens table
    }

    @Override
    public boolean changeUserPassword(User user, String newPassword) {
        user.setPassword(newPassword); // Store as plain text
        user.setUpdatedAt(LocalDateTime.now());
        userService.save(user);
        return true;
    }
}