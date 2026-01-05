package com.santoprestandrea_s00007624.backend_travelmates.controller;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.RegisterRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateUserRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.UserResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import com.santoprestandrea_s00007624.backend_travelmates.mapper.UserMapper;
import com.santoprestandrea_s00007624.backend_travelmates.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * GET /api/users/me
     * Get own profile (authenticated user)
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        UserResponse response = userMapper.toResponse(currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/users/me
     * Update own profile
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody UpdateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        userMapper.updateEntityFromRequest(request, currentUser);
        User updated = userService.updateUser(currentUser.getId(), currentUser);

        UserResponse response = userMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/users/{id}
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.findByIdOrThrow(id);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/users
     * List all users (ADMIN only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/users/active
     * Get active users only
     */
    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        List<User> users = userService.findActiveUsers();
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * PATCH /api/users/{id}/deactivate
     * Deactivate account
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/users/{id}/activate
     * Reactivate account
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/users/count
     * Count all users
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> countUsers() {
        long count = userService.countUsers();
        return ResponseEntity.ok(count);
    }
}
