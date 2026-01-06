package com.santoprestandrea_s00007624.backend_travelmates.dto.response;


import com.santoprestandrea_s00007624.backend_travelmates.entity.MemberRole;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO - TRIP MEMBER RESPONSE
 *
 * Represents a trip participant.
 *
 * JSON EXAMPLE:
 * {
 * "id": 1,
 * "user": {
 * "id": 5,
 * "email": "mario@example.com",
 * "firstName": "Mario",
 * "lastName": "Rossi"
 * },
 * "role": "ORGANIZER",
 * "joinedAt": "2025-01-01T10:00:00",
 * "invitationStatus": "ACCEPTED"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripMemberResponse {

    private Long id;

    /**
     * USER DATA
     *
     * Includes only essential data (no password!)
     */
    private UserResponse user;

    /**
     * ROLE IN THE TRIP
     */
    private MemberRole role;

    /**
     * WHEN JOINED
     */
    private LocalDateTime joinedAt;

    /**
     * INVITATION STATUS
     */
    private String invitationStatus;
}