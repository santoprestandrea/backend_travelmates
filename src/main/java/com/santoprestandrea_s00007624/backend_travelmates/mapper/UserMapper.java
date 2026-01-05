package com.santoprestandrea_s00007624.backend_travelmates.mapper;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.RegisterRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateUserRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.UserResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Converts User entity to UserResponse DTO
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .phoneNumber(user.getPhoneNumber())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .registrationDate(user.getRegistrationDate())
                .lastLogin(user.getLastLogin())
                .isActive(user.getIsActive())
                .build();
    }

    /**
     * Converts RegisterRequest to User entity
     */
    public User toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .bio(request.getBio())
                .build();
    }

    /**
     * Updates User entity from UpdateUserRequest
     */
    public void updateEntityFromRequest(UpdateUserRequest request, User user) {
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }
    }
}
