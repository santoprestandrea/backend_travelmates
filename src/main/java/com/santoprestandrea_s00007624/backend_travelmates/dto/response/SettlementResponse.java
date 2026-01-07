package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import com.santoprestandrea_s00007624.backend_travelmates.entity.SettlementStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO - SETTLEMENT RESPONSE
 *
 * Represents a settlement in API responses.
 *
 * JSON EXAMPLE:
 * {
 * "id": 1,
 * "tripId": 10,
 * "fromUser": {
 * "id": 1,
 * "firstName": "Mario",
 * "lastName": "Rossi"
 * },
 * "toUser": {
 * "id": 5,
 * "firstName": "Luca",
 * "lastName": "Bianchi"
 * },
 * "amount": 50.00,
 * "currency": "EUR",
 * "status": "PENDING",
 * "createdAt": "2025-01-07T14:30:00",
 * "settledAt": null,
 * "notes": "Payment for shared dinner"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementResponse {

    private Long id;
    private Long tripId;
    private UserResponse fromUser;
    private UserResponse toUser;
    private BigDecimal amount;
    private String currency;
    private SettlementStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime settledAt;
    private String notes;
}
