package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import com.santoprestandrea_s00007624.backend_travelmates.entity.ExpenseCategory;
import com.santoprestandrea_s00007624.backend_travelmates.entity.SplitType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO - CREATE SHARED EXPENSE REQUEST
 *
 * Used to create a shared expense that will be split among multiple users.
 *
 * JSON EXAMPLE (EQUAL split):
 * {
 * "description": "Dinner at restaurant",
 * "amount": 120.00,
 * "currency": "EUR",
 * "category": "FOOD",
 * "date": "2025-06-15",
 * "splitType": "EQUAL",
 * "participantIds": [1, 2, 3, 4]
 * }
 *
 * JSON EXAMPLE (PERCENTAGE split):
 * {
 * "description": "Hotel booking",
 * "amount": 200.00,
 * "currency": "EUR",
 * "category": "ACCOMMODATION",
 * "date": "2025-06-10",
 * "splitType": "PERCENTAGE",
 * "splits": [
 * {"userId": 1, "percentage": 50.00},
 * {"userId": 2, "percentage": 30.00},
 * {"userId": 3, "percentage": 20.00}
 * ]
 * }
 *
 * JSON EXAMPLE (CUSTOM split):
 * {
 * "description": "Shopping",
 * "amount": 100.00,
 * "currency": "EUR",
 * "category": "SHOPPING",
 * "date": "2025-06-12",
 * "splitType": "CUSTOM",
 * "splits": [
 * {"userId": 1, "amount": 60.00},
 * {"userId": 2, "amount": 40.00}
 * ]
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSharedExpenseRequest {

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description too long")
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters (ISO 4217)")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be uppercase ISO code (e.g., EUR, USD)")
    private String currency;

    @NotNull(message = "Category is required")
    private ExpenseCategory category;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Expense date cannot be in the future")
    private LocalDate date;

    @NotNull(message = "Split type is required")
    private SplitType splitType;

    /**
     * For EQUAL split: just list of participant user IDs
     */
    private List<Long> participantIds;

    /**
     * For PERCENTAGE or CUSTOM split: detailed split information
     */
    private List<SplitDetailRequest> splits;

    @Size(max = 500, message = "Receipt URL too long")
    private String receiptImageUrl;

    @Size(max = 1000, message = "Notes too long")
    private String notes;

    /**
     * Nested class for split details
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SplitDetailRequest {

        @NotNull(message = "User ID is required")
        private Long userId;

        /**
         * For PERCENTAGE split
         */
        @DecimalMin(value = "0.01", message = "Percentage must be greater than 0")
        @DecimalMax(value = "100.00", message = "Percentage cannot exceed 100")
        private BigDecimal percentage;

        /**
         * For CUSTOM split
         */
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;
    }
}
