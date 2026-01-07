package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO - TRIP NOTE RESPONSE
 *
 * Represents a note in API responses.
 *
 * JSON EXAMPLE:
 * {
 * "id": 1,
 * "tripId": 10,
 * "author": {
 * "id": 1,
 * "firstName": "Mario",
 * "lastName": "Rossi"
 * },
 * "content": "Don't forget to bring your passport!",
 * "isPinned": true,
 * "createdAt": "2025-01-07T14:30:00",
 * "updatedAt": "2025-01-07T15:00:00"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripNoteResponse {

    private Long id;
    private Long tripId;
    private UserResponse author;
    private String content;
    private Boolean isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
