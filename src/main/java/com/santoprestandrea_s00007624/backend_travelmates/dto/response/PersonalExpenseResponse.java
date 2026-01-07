package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import lombok.*;

/**
 * DTO - PERSONAL EXPENSE RESPONSE
 *
 * One-to-one expense between two users.
 *
 * JSON EXAMPLE:
 * {
 * "id": 2,
 * "tripId": 10,
 * "paidBy": { "id": 1, "firstName": "Mario", ... },
 * "forUser": { "id": 5, "firstName": "Luca", ... },
 * "amount": 50.00,
 * "currency": "EUR",
 * "description": "Train ticket for Luca",
 * "date": "2025-06-10",
 * "category": "TRANSPORT",
 * "expenseType": "PERSONAL",
 * "isPaid": false
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalExpenseResponse extends ExpenseResponse {

    private UserResponse forUser;
    private Boolean isPaid;

    @Builder(builderMethodName = "personalExpenseBuilder")
    public PersonalExpenseResponse(
            Long id,
            Long tripId,
            UserResponse paidBy,
            java.math.BigDecimal amount,
            String currency,
            String description,
            java.time.LocalDate date,
            com.santoprestandrea_s00007624.backend_travelmates.entity.ExpenseCategory category,
            String receiptImageUrl,
            String notes,
            java.time.LocalDateTime createdAt,
            UserResponse createdBy,
            String expenseType,
            UserResponse forUser,
            Boolean isPaid) {
        super(id, tripId, paidBy, amount, currency, description, date, category,
                receiptImageUrl, notes, createdAt, createdBy, expenseType);
        this.forUser = forUser;
        this.isPaid = isPaid;
    }
}
