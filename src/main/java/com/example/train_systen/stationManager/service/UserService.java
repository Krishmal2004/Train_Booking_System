package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.User;
import com.example.train_systen.stationManager.dto.SignupRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAllUsers();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User registerNewUser(SignupRequest signupRequest);

    void updateLastLogin(User user);

    User save(User user);

    void deleteById(Long id);

    long countUsersByRole(String roleName);
}