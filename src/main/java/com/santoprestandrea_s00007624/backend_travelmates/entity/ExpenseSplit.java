package com.santoprestandrea_s00007624.backend_travelmates.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * EXPENSE SPLIT
 *
 * Represents how much each person owes for a SharedExpense.
 *
 * EXAMPLE (EQUAL split):
 * SharedExpense: €120 dinner
 * - Split 1: Mario → €30
 * - Split 2: Luca → €30
 * - Split 3: Anna → €30
 * - Split 4: Sofia → €30
 *
 * EXAMPLE (PERCENTAGE split):
 * SharedExpense: €100 hotel
 * - Split 1: Mario → 50% = €50
 * - Split 2: Luca → 30% = €30
 * - Split 3: Anna → 20% = €20
 */
@Entity
@Table(name = "expense_splits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The shared expense this split belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_expense_id", nullable = false)
    private SharedExpense sharedExpense;

    /**
     * User who owes this amount
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Amount this user owes (calculated based on split type)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Percentage for PERCENTAGE split type (0-100)
     * Null for EQUAL and CUSTOM splits
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    /**
     * Whether this user has paid their share
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isPaid = false;
}
