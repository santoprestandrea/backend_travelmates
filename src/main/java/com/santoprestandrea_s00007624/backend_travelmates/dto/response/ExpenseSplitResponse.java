package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO - EXPENSE SPLIT RESPONSE
 *
 * Represents how much a user owes for a shared expense.
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
 * "amount": 30.00,
 * "percentage": null,
 * "isPaid": false
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSplitResponse {

    private Long id;
    private UserResponse user;
    private BigDecimal amount;
    private BigDecimal percentage;
    private Boolean isPaid;
}
