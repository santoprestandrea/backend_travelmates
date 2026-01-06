package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import com.santoprestandrea_s00007624.backend_travelmates.entity.TripStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO - TRIP RESPONSE (BASE VERSION)
 *
 * Essential trip data (without members).
 * Used for trip lists.
 *
 * JSON EXAMPLE:
 * {
 * "id": 1,
 * "title": "Vacation in Paris",
 * "destination": "Paris, France",
 * "startDate": "2025-06-01",
 * "endDate": "2025-06-05",
 * "status": "PLANNING",
 * "memberCount": 3,
 * "createdAt": "2025-01-01T10:00:00"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResponse {

    private Long id;
    private String title;
    private String description;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private String currency;
    private String coverImageUrl;
    private TripStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Number of members (calculated)
     *
     * We don't include the complete member list here
     * (too much data for a trip list).
     */
    private Integer memberCount;

    /**
     * Duration in days (calculated)
     */
    private Long durationInDays;
}
