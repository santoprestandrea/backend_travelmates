package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import com.santoprestandrea_s00007624.backend_travelmates.entity.ExpenseCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO - EXPENSE RESPONSE (BASE)
 *
 * Base response for all expense types.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {

    private Long id;
    private Long tripId;
    private UserResponse paidBy;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDate date;
    private ExpenseCategory category;
    private String receiptImageUrl;
    private String notes;
    private LocalDateTime createdAt;
    private UserResponse createdBy;

    /**
     * Discriminator: "SHARED" or "PERSONAL"
     */
    private String expenseType;
}
