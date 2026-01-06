package com.santoprestandrea_s00007624.backend_travelmates.dto.response;



import com.santoprestandrea_s00007624.backend_travelmates.entity.TripStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO - DETAILED TRIP RESPONSE
 *
 * Includes ALL trip data + member list.
 * Used for the trip detail page.
 *
 * JSON EXAMPLE:
 * {
 * "id": 1,
 * "title": "Vacation in Paris",
 * ...
 * "members": [
 * {
 * "id": 1,
 * "user": { "id": 5, "firstName": "Mario", ... },
 * "role": "ORGANIZER",
 * "joinedAt": "2025-01-01T10:00:00"
 * },
 * ...
 * ]
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDetailResponse {

    // BASE DATA (same as TripResponse)

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
    private Long durationInDays;

    // EXTRA DATA (detail only)

    /**
     * COMPLETE MEMBER LIST
     *
     * Includes all participants with their data.
     */
    private List<TripMemberResponse> members;

    /**
     * STATISTICS (optional, for PHASE 2)
     *
     * Example: total expenses, number of activities, etc.
     * For now leave null.
     */
    private TripStatisticsResponse statistics;
}
