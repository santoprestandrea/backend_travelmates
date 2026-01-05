package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name is too long")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name is too long")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 20, message = "Phone number is too long")
    private String phoneNumber;

    @Size(max = 500, message = "Bio is too long")
    private String bio;
}
