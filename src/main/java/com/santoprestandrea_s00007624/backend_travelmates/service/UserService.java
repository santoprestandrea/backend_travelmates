package com.santoprestandrea_s00007624.backend_travelmates.service;

import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import com.santoprestandrea_s00007624.backend_travelmates.entity.UserRole;
import com.santoprestandrea_s00007624.backend_travelmates.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.santoprestandrea_s00007624.backend_travelmates.exception.EmailAlreadyExistsException;
import com.santoprestandrea_s00007624.backend_travelmates.exception.ResourceNotFoundException;
import java.time.LocalDateTime;

import java.util.Optional;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // Constructor Injection (best practice)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ===== CREATE =====
    /**
     * Registers a new user
     */
    public User registerUser(User user) {
        // Custom validation with custom exception
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered: " + user.getEmail());
        }

        // Set default values
        if (user.getRole() == null) {
            user.setRole(UserRole.TRAVELER);
        }
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        // PASSWORD ENCRYPTION IS STILL MISSING HERE!

        return userRepository.save(user);
    }

    // ===== READ =====
    /**
     * Finds user by ID or throws exception if not found
     */
    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + id));
    }

    /**
     * Finds user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds all users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Finds active users
     */
    public List<User> findActiveUsers() {
        return userRepository.findByIsActive(true);
    }

    /**
     * Finds users by role
     */
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    // ===== UPDATE =====
    /**
     * Updates user profile
     */
    public User updateUser(Long id, User updatedData) {
        User existingUser = findByIdOrThrow(id);

        // Update only non-null fields
        if (updatedData.getFirstName() != null) {
            existingUser.setFirstName(updatedData.getFirstName());
        }
        if (updatedData.getLastName() != null) {
            existingUser.setLastName(updatedData.getLastName());
        }
        if (updatedData.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updatedData.getDateOfBirth());
        }
        if (updatedData.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedData.getPhoneNumber());
        }
        if (updatedData.getBio() != null) {
            existingUser.setBio(updatedData.getBio());
        }

        return userRepository.save(existingUser);
    }

    public User updateProfileImage(Long id, String imageUrl) {
        User user = findByIdOrThrow(id);
        user.setProfileImageUrl(imageUrl);
        return userRepository.save(user);
    }

    public void deactivateUser(Long id) {
        User user = findByIdOrThrow(id);
        user.setIsActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long id) {
        User user = findByIdOrThrow(id);
        user.setIsActive(true);
        userRepository.save(user);
    }

    // ===== DELETE =====
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // ===== UTILITY =====
    public long countUsers() {
        return userRepository.count();
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * SAVE USER (generic)
     * Used by AuthController to save user after registration
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * UPDATE LAST LOGIN
     * Called when user logs in
     */
    public void updateLastLogin(Long userId) {
        User user = findByIdOrThrow(userId);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * CHECK IF EMAIL EXISTS
     * Used during registration
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
