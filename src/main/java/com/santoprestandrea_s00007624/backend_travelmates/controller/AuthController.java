package com.santoprestandrea_s00007624.backend_travelmates.controller;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.LoginRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.RegisterRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.LoginResponse;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.UserResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import com.santoprestandrea_s00007624.backend_travelmates.entity.UserRole;
import com.santoprestandrea_s00007624.backend_travelmates.exception.EmailAlreadyExistsException;
import com.santoprestandrea_s00007624.backend_travelmates.mapper.UserMapper;
import com.santoprestandrea_s00007624.backend_travelmates.service.JwtService;
import com.santoprestandrea_s00007624.backend_travelmates.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for authentication operations (register, login, current user)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    /**
     * Register a new user
     * @param request Registration data (email, password, personal info)
     * @return LoginResponse with JWT token and user data, or error message
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.info("Registration attempt for email: {}", request.getEmail());

            // Check if email already exists
            if (userService.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
            }

            // Create new user with encoded password and default role
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .dateOfBirth(request.getDateOfBirth())
                    .phoneNumber(request.getPhoneNumber())
                    .bio(request.getBio())
                    .role(UserRole.TRAVELER) // Default role for new users
                    .isActive(true)
                    .build();

            // Save user and generate JWT token
            User savedUser = userService.save(user);
            String token = jwtService.generateToken(savedUser);
            UserResponse userResponse = userMapper.toResponse(savedUser);

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .user(userResponse)
                    .build();

            logger.info("User successfully registered: {}", savedUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EmailAlreadyExistsException e) {
            logger.warn("Registration attempt with existing email: {}", request.getEmail());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error during registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Authenticate user and generate JWT token
     * @param request Login credentials (email and password)
     * @return LoginResponse with JWT token and user data, or error message
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            logger.info("Login attempt for email: {}", request.getEmail());

            // Find user by email
            User user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            // Check if account is active
            if (!user.getIsActive()) {
                throw new RuntimeException("Account disabled");
            }

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                logger.warn("Wrong password for user: {}", request.getEmail());
                throw new RuntimeException("Invalid credentials");
            }

            // Update last login timestamp and generate token
            userService.updateLastLogin(user.getId());
            String token = jwtService.generateToken(user);
            UserResponse userResponse = userMapper.toResponse(user);

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .user(userResponse)
                    .build();

            logger.info("Login successful for user: {}", user.getEmail());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.warn("Login failed for email: {}", request.getEmail());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get current authenticated user information
     * @param authHeader Authorization header containing JWT token
     * @return Current user data or unauthorized status
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from "Bearer <token>" format
            String token = authHeader.substring(7);
            String email = jwtService.getEmailFromToken(token);

            // Retrieve user from database
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserResponse userResponse = userMapper.toResponse(user);
            return ResponseEntity.ok(userResponse);

        } catch (Exception e) {
            logger.error("Error retrieving current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
