package com.santoprestandrea_s00007624.backend_travelmates.service;

import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import com.santoprestandrea_s00007624.backend_travelmates.entity.UserRole;
import com.santoprestandrea_s00007624.backend_travelmates.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Registra un nuovo utente
     */
    public User registerUser(User user) {
        // Validazione: email già esistente?
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email già registrata: " + user.getEmail());
        }

        // Imposta valori di default
        if (user.getRole() == null) {
            user.setRole(UserRole.TRAVELER);
        }
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        // TODO: Criptare password (vedremo dopo con BCrypt)
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Salva nel database
        return userRepository.save(user);
    }

    // ===== READ =====

    /**
     * Trova utente per ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Trova utente per ID o lancia eccezione
     */
    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + id));
    }

    /**
     * Trova utente per email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Trova tutti gli utenti
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Trova utenti attivi
     */
    public List<User> findActiveUsers() {
        return userRepository.findByIsActive(true);
    }

    /**
     * Trova utenti per ruolo
     */
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    // ===== UPDATE =====

    /**
     * Aggiorna profilo utente
     */
    public User updateUser(Long id, User updatedData) {
        User existingUser = findByIdOrThrow(id);

        // Aggiorna solo i campi non nulli
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

    /**
     * Aggiorna immagine profilo
     */
    public User updateProfileImage(Long id, String imageUrl) {
        User user = findByIdOrThrow(id);
        user.setProfileImageUrl(imageUrl);
        return userRepository.save(user);
    }

    /**
     * Disattiva account (soft delete)
     */
    public void deactivateUser(Long id) {
        User user = findByIdOrThrow(id);
        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Riattiva account
     */
    public void activateUser(Long id) {
        User user = findByIdOrThrow(id);
        user.setIsActive(true);
        userRepository.save(user);
    }

    // ===== DELETE =====

    /**
     * Elimina utente permanentemente (hard delete)
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utente non trovato con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // ===== UTILITY =====

    /**
     * Conta tutti gli utenti
     */
    public long countUsers() {
        return userRepository.count();
    }

    /**
     * Controlla se email esiste
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}

