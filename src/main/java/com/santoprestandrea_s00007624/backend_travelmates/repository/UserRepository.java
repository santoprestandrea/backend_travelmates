package com.santoprestandrea_s00007624.backend_travelmates.repository;

import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import com.santoprestandrea_s00007624.backend_travelmates.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by email
    // Spring automatically generates: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Check if a user exists with that email
    // Spring generates: SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);

    // Find all active users
    // Spring generates: SELECT * FROM users WHERE is_active = true
    // Returns a list of users
    List<User> findByIsActive(Boolean isActive);

    // Find users by role
    // Spring generates: SELECT * FROM users WHERE role = ?
    List<User> findByRole(UserRole role);
}
