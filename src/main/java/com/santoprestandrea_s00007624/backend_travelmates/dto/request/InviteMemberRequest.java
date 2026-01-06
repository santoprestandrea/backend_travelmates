package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import com.santoprestandrea_s00007624.backend_travelmates.entity.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO - INVITE MEMBER REQUEST
 *
 * Used to invite a user to a trip.
 *
 * JSON EXAMPLE:
 * {
 * "userEmail": "mario@example.com",
 * "role": "PARTICIPANT"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteMemberRequest {

    /**
     * Email of the user to invite
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String userEmail;

    /**
     * Role in the trip: ORGANIZER or PARTICIPANT
     *
     * Default: PARTICIPANT
     */
    @NotNull(message = "Role is required")
    @Builder.Default
    private MemberRole role = MemberRole.PARTICIPANT;
}
