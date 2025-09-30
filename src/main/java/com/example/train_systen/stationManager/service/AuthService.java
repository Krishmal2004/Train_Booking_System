package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.User;
import com.example.train_systen.stationManager.dto.LoginRequest;
import com.example.train_systen.stationManager.dto.SignupRequest;

import org.springframework.validation.BindingResult;

public interface AuthService {

    User authenticateUser(LoginRequest loginRequest) throws Exception;

    User registerUser(SignupRequest signupRequest, BindingResult result);

    boolean validatePasswordResetToken(String token);

    void createPasswordResetTokenForUser(User user, String token);

    boolean changeUserPassword(User user, String newPassword);
}