package com.santoprestandrea_s00007624.backend_travelmates.dto.request;


import com.santoprestandrea_s00007624.backend_travelmates.entity.TripStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO - UPDATE TRIP REQUEST
 *
 * All fields are OPTIONAL!
 * Only what is sent will be updated.
 *
 * JSON EXAMPLE (updates only title and budget):
 * {
 * "title": "New title",
 * "budget": 2000.00
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTripRequest {

    @Size(max = 100, message = "Title too long")
    private String title;

    @Size(max = 1000, message = "Description too long")
    private String description;

    @Size(max = 100, message = "Destination too long")
    private String destination;

    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @FutureOrPresent(message = "End date cannot be in the past")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be positive")
    @Digits(integer = 8, fraction = 2, message = "Invalid budget")
    private BigDecimal budget;

    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;

    @Size(max = 500, message = "Image URL too long")
    private String coverImageUrl;

    /**
     * TRIP STATUS
     *
     * Can only be updated by ORGANIZERS.
     * Valid transitions:
     * - PLANNING → ACTIVE
     * - ACTIVE → COMPLETED
     * - Any → CANCELLED
     */
    private TripStatus status;
}
