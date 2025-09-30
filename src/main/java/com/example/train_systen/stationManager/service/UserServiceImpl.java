package com.example.train_systen.stationManager.service;

import com.example.train_systen.stationManager.model.Role;
import com.example.train_systen.stationManager.model.User;
import com.example.train_systen.stationManager.dto.SignupRequest;
import com.example.train_systen.stationManager.repository.RoleRepository;
import com.example.train_systen.stationManager.repository.UserRepository;
import com.example.train_systen.stationManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User registerNewUser(SignupRequest signupRequest) {
        // Create new user
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword()); // Store password as plain text without encoding
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPhoneNumber(signupRequest.getPhoneNumber());
        user.setDateOfBirth(signupRequest.getDateOfBirth());
        user.setGender(signupRequest.getGender());
        user.setPreferredSeat(signupRequest.getPreferredSeat());
        user.setPreferredClass(signupRequest.getPreferredClass());
        user.setEmailNotifications(signupRequest.isEmailNotifications());
        user.setSmsNotifications(signupRequest.isSmsNotifications());
        user.setMarketingEmails(signupRequest.isMarketingEmails());

        // Set role as PASSENGER by default
        Set<Role> roles = new HashSet<>();
        Role passengerRole = roleRepository.findByName("ROLE_PASSENGER")
                .orElseThrow(() -> new RuntimeException("Error: Role PASSENGER is not found."));
        roles.add(passengerRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public long countUsersByRole(String roleName) {
        return userRepository.countByRoleName(roleName);
    }
}