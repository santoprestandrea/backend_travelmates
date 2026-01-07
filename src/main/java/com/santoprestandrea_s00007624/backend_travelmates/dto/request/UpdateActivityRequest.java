package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import com.santoprestandrea_s00007624.backend_travelmates.entity.ActivityCategory;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO - UPDATE ACTIVITY REQUEST
 *
 * All fields are OPTIONAL!
 * Only provided fields will be updated.
 *
 * JSON EXAMPLE (updates only title and cost):
 * {
 * "title": "Updated Colosseum Tour",
 * "cost": 30.00
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateActivityRequest {

    @Size(max = 200, message = "Title too long")
    private String title;

    @Size(max = 1000, message = "Description too long")
    private String description;

    private LocalDateTime scheduledDate;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 10080, message = "Duration cannot exceed 7 days (10080 minutes)")
    private Integer duration;

    @Size(max = 200, message = "Location too long")
    private String location;

    private ActivityCategory category;

    @DecimalMin(value = "0.0", inclusive = false, message = "Cost must be positive")
    @Digits(integer = 8, fraction = 2, message = "Invalid cost format")
    private BigDecimal cost;

    @Size(min = 3, max = 3, message = "Currency must be 3 characters (e.g., EUR, USD)")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be uppercase letters (e.g., EUR)")
    private String currency;

    @Size(max = 500, message = "Booking URL too long")
    @Pattern(regexp = "^(https?://).*", message = "Booking URL must start with http:// or https://")
    private String bookingUrl;

    @Size(max = 100, message = "Booking reference too long")
    private String bookingReference;

    private Boolean isConfirmed;

    private Boolean isCancelled;

    @Size(max = 1000, message = "Notes too long")
    private String notes;
}
