package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import com.santoprestandrea_s00007624.backend_travelmates.entity.ExpenseCategory;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO - UPDATE EXPENSE REQUEST
 *
 * All fields are OPTIONAL!
 * Only provided fields will be updated.
 *
 * JSON EXAMPLE:
 * {
 * "description": "Updated dinner description",
 * "amount": 150.00,
 * "notes": "Added tip"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateExpenseRequest {

    @Size(max = 500, message = "Description too long")
    private String description;

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;

    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be uppercase ISO code")
    private String currency;

    private ExpenseCategory category;

    @PastOrPresent(message = "Expense date cannot be in the future")
    private LocalDate date;

    @Size(max = 500, message = "Receipt URL too long")
    private String receiptImageUrl;

    @Size(max = 1000, message = "Notes too long")
    private String notes;
}
