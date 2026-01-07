package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO - CREATE SETTLEMENT REQUEST
 *
 * Used to create a new settlement (payment) between users.
 *
 * JSON EXAMPLE:
 * {
 * "toUserId": 5,
 * "amount": 50.00,
 * "currency": "EUR",
 * "notes": "Payment for shared dinner"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSettlementRequest {

    @NotNull(message = "To user ID is required")
    private Long toUserId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters (e.g., EUR, USD)")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be uppercase letters (e.g., EUR)")
    private String currency;

    @Size(max = 1000, message = "Notes too long")
    private String notes;
}
