package com.santoprestandrea_s00007624.backend_travelmates.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SETTLEMENT ENTITY - Represents a payment between users
 *
 * Settlements track money transfers between trip members to settle debts.
 * Example: Mario owes €50 to Luca, so a settlement is created for Mario to pay
 * Luca.
 */
@Entity
@Table(name = "settlements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * TRIP ASSOCIATION
     * Each settlement belongs to a specific trip
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    /**
     * FROM USER - User who owes money
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    /**
     * TO USER - User who should receive money
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    /**
     * AMOUNT
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    /**
     * STATUS
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettlementStatus status;

    /**
     * TIMESTAMPS
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    /**
     * NOTES
     */
    @Column(length = 1000)
    private String notes;
}
