package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import com.santoprestandrea_s00007624.backend_travelmates.entity.SplitType;
import lombok.*;

import java.util.List;

/**
 * DTO - SHARED EXPENSE RESPONSE
 *
 * Includes split information for shared expenses.
 *
 * JSON EXAMPLE:
 * {
 * "id": 1,
 * "tripId": 10,
 * "paidBy": { "id": 1, "firstName": "Mario", ... },
 * "amount": 120.00,
 * "currency": "EUR",
 * "description": "Dinner at restaurant",
 * "date": "2025-06-15",
 * "category": "FOOD",
 * "expenseType": "SHARED",
 * "splitType": "EQUAL",
 * "splits": [
 * { "id": 1, "user": {...}, "amount": 30.00, "isPaid": false },
 * { "id": 2, "user": {...}, "amount": 30.00, "isPaid": true }
 * ]
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SharedExpenseResponse extends ExpenseResponse {

    private SplitType splitType;
    private List<ExpenseSplitResponse> splits;

    @Builder(builderMethodName = "sharedExpenseBuilder")
    public SharedExpenseResponse(
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
            SplitType splitType,
            List<ExpenseSplitResponse> splits) {
        super(id, tripId, paidBy, amount, currency, description, date, category,
                receiptImageUrl, notes, createdAt, createdBy, expenseType);
        this.splitType = splitType;
        this.splits = splits;
    }
}
