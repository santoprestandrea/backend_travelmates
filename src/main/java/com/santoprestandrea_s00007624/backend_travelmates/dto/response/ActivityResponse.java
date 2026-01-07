package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import com.santoprestandrea_s00007624.backend_travelmates.entity.ActivityCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO - ACTIVITY RESPONSE
 *
 * Represents an activity in API responses.
 *
 * JSON EXAMPLE:
 * {
 * "id": 1,
 * "tripId": 10,
 * "title": "Visit Colosseum",
 * "description": "Guided tour of the Colosseum",
 * "scheduledDate": "2025-06-15T10:00:00",
 * "duration": 120,
 * "location": "Piazza del Colosseo, Rome",
 * "category": "SIGHTSEEING",
 * "cost": 25.00,
 * "currency": "EUR",
 * "bookingUrl": "https://example.com/booking",
 * "bookingReference": "COL123456",
 * "isConfirmed": true,
 * "isCancelled": false,
 * "createdBy": {
 * "id": 1,
 * "firstName": "Mario",
 * "lastName": "Rossi"
 * },
 * "createdAt": "2025-01-05T14:30:00",
 * "updatedAt": "2025-01-06T10:15:00",
 * "notes": "Bring ID for student discount"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityResponse {

    private Long id;
    private Long tripId;
    private String title;
    private String description;
    private LocalDateTime scheduledDate;
    private Integer duration;
    private String location;
    private ActivityCategory category;
    private BigDecimal cost;
    private String currency;
    private String bookingUrl;
    private String bookingReference;
    private Boolean isConfirmed;
    private Boolean isCancelled;
    private UserResponse createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
}
