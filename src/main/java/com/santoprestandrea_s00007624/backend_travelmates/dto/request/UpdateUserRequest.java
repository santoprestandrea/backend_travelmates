package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    // All fields are optional (only update provided ones)

    @Size(max = 50, message = "First name too long")
    private String firstName;

    @Size(max = 50, message = "Last name too long")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 20, message = "Phone number too long")
    private String phoneNumber;

    @Size(max = 500, message = "Bio too long")
    private String bio;

    private String profileImageUrl;
}
