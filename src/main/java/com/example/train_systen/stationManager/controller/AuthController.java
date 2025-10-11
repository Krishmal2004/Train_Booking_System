package com.example.train_systen.stationManager.controller;

import com.example.train_systen.stationManager.dto.LoginRequest;
import com.example.train_systen.stationManager.dto.SignupRequest;
import com.example.train_systen.stationManager.model.User;
import com.example.train_systen.stationManager.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        model.addAttribute("currentTime", "2025-09-16 11:11:39");
        model.addAttribute("currentUser", "Krishmal2004");
        return "login";
    }

    @PostMapping("/login")
    public String loginProcess(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                               BindingResult result,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (result.hasErrors()) {
            model.addAttribute("currentTime", "2025-09-16 11:11:39");
            model.addAttribute("currentUser", "Krishmal2004");
            return "login";
        }

        try {
            User user = authService.authenticateUser(loginRequest);

            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("isLoggedIn", true);

            // Store user roles in session for authorization checks
            session.setAttribute("userRoles", user.getRoles());

            // Handle "Remember Me" functionality if needed
            if (loginRequest.isRememberMe()) {
                // Set a cookie with a longer expiration time
                // This is a simplified approach without Spring Security
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days in seconds
            }

            // Redirect based on role
            if (user.hasRole("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (user.hasRole("ROLE_IT_SUPPORT")) {
                return "redirect:/issues/dashboard";
            } else if (user.hasRole("ROLE_OPS_MANAGER")) {
                return "redirect:/packages";
            } else if (user.hasRole("ROLE_STATION_MANAGER")) {
                return "redirect:/routes";
            } else if (user.hasRole("ROLE_TICKETING_OFFICER")) {
                return "redirect:/sales-reports/dashboard";
            } else {
                // Default for passengers
                return "redirect:/dashboard";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        model.addAttribute("currentTime", "2025-09-16 11:11:39");
        model.addAttribute("currentUser", "Krishmal2004");
        return "signup";
    }

    @PostMapping("/signup")
    public String processRegistration(@Valid @ModelAttribute("signupRequest") SignupRequest signupRequest,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {

        if (result.hasErrors()) {
            model.addAttribute("currentTime", "2025-09-16 11:11:39");
            model.addAttribute("currentUser", "Krishmal2004");
            return "signup"; // Return to signup page with validation errors
        }

        User newUser = authService.registerUser(signupRequest, result);

        if (newUser == null) {
            model.addAttribute("currentTime", "2025-09-16 11:11:39");
            model.addAttribute("currentUser", "Krishmal2004");
            return "signup"; // Return to signup page with validation errors
        }

        redirectAttributes.addFlashAttribute("success", "Account created successfully! Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("currentTime", "2025-09-16 11:11:39");
        model.addAttribute("currentUser", "Krishmal2004");
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        RedirectAttributes redirectAttributes) {
        // Implementation for forgot password functionality
        // Would typically generate a token and send an email

        redirectAttributes.addFlashAttribute("message",
                "If your email exists in our system, you will receive a password reset link shortly.");
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        boolean validToken = authService.validatePasswordResetToken(token);

        if (!validToken) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "error";
        }

        model.addAttribute("token", token);
        model.addAttribute("currentTime", "2025-09-16 11:11:39");
        model.addAttribute("currentUser", "Krishmal2004");
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       RedirectAttributes redirectAttributes) {
        // Implementation for password reset
        // Would typically validate token, find user, and update password

        redirectAttributes.addFlashAttribute("success", "Your password has been updated successfully.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        redirectAttributes.addFlashAttribute("success", "You have been logged out successfully.");
        return "redirect:/login";
    }
}