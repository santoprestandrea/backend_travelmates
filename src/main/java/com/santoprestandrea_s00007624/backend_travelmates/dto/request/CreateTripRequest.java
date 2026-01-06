package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO - CREATE TRIP REQUEST
 *
 * Data required to create a new trip.
 *
 * JSON EXAMPLE:
 * {
 * "title": "Paris Vacation",
 * "description": "Romantic weekend in the city of love",
 * "destination": "Paris, France",
 * "startDate": "2025-06-01",
 * "endDate": "2025-06-05",
 * "budget": 1500.00,
 * "currency": "EUR"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTripRequest {

    /**
     * Trip title (e.g.: "Paris Vacation")
     */
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title too long (max 100 characters)")
    private String title;

    /**
     * Detailed description (optional)
     */
    @Size(max = 1000, message = "Description too long (max 1000 characters)")
    private String description;

    /**
     * Main destination
     */
    @NotBlank(message = "Destination is required")
    @Size(max = 100, message = "Destination too long")
    private String destination;

    /**
     * Start date
     *
     * Validation: must be in the future (or today)
     */
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    /**
     * End date
     *
     * Validation: must be in the future (or today)
     * NOTE: We will also check that endDate >= startDate in the Service!
     */
    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date cannot be in the past")
    private LocalDate endDate;

    /**
     * Total budget (optional)
     *
     * Validation: if provided, must be positive
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be positive")
    @Digits(integer = 8, fraction = 2, message = "Invalid budget (max 8 integer digits, 2 decimal)")
    private BigDecimal budget;

    /**
     * Currency (optional, default EUR)
     *
     * Examples: "EUR", "USD", "GBP"
     */
    @Size(min = 3, max = 3, message = "Currency must be 3 characters (e.g.: EUR)")
    private String currency;

    /**
     * Cover image URL (optional)
     */
    @Size(max = 500, message = "Image URL too long")
    private String coverImageUrl;
}
