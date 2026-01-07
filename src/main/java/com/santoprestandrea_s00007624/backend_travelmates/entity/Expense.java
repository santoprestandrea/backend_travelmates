package com.santoprestandrea_s00007624.backend_travelmates.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * EXPENSE - BASE CLASS (ABSTRACT)
 *
 * Represents a generic expense in a trip.
 * Uses JOINED inheritance strategy for SharedExpense and PersonalExpense.
 *
 * INHERITANCE HIERARCHY:
 * Expense (abstract)
 *   ├── SharedExpense (split among multiple users)
 *   └── PersonalExpense (one-to-one reimbursement)
 */
@Entity
@Table(name = "expenses")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "expense_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Trip this expense belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    /**
     * User who paid for this expense
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_user_id", nullable = false)
    private User paidBy;

    /**
     * Amount paid
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Currency code (ISO 4217: EUR, USD, GBP, etc.)
     */
    @Column(nullable = false, length = 3)
    private String currency;

    /**
     * Description of the expense
     */
    @Column(nullable = false, length = 500)
    private String description;

    /**
     * Date when the expense occurred
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Category of the expense
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpenseCategory category;

    /**
     * Optional URL to receipt image
     */
    @Column(length = 500)
    private String receiptImageUrl;

    /**
     * Timestamp when expense was created
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * User who created this expense entry
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    /**
     * Additional notes
     */
    @Column(length = 1000)
    private String notes;
}
