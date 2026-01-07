package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import com.santoprestandrea_s00007624.backend_travelmates.entity.ExpenseCategory;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO - CREATE PERSONAL EXPENSE REQUEST
 *
 * Used to create a personal expense (one person paid for another).
 *
 * JSON EXAMPLE:
 * {
 * "description": "Train ticket for Luca",
 * "amount": 50.00,
 * "currency": "EUR",
 * "category": "TRANSPORT",
 * "date": "2025-06-10",
 * "forUserId": 5,
 * "notes": "Paid for Luca's train ticket"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePersonalExpenseRequest {

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

    /**
     * ID of the user who should reimburse this expense
     */
    @NotNull(message = "User ID is required")
    private Long forUserId;

    @Size(max = 500, message = "Receipt URL too long")
    private String receiptImageUrl;

    @Size(max = 1000, message = "Notes too long")
    private String notes;
}
