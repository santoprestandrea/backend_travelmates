package com.santoprestandrea_s00007624.backend_travelmates.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * PERSONAL EXPENSE
 *
 * An expense where one person paid for another person.
 * Direct one-to-one reimbursement.
 *
 * EXAMPLE:
 * - Mario buys train ticket for Luca: €50
 * - Paid by: Mario
 * - For user: Luca
 * - Luca owes Mario €50
 */
@Entity
@Table(name = "personal_expenses")
@DiscriminatorValue("PERSONAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalExpense extends Expense {

    /**
     * The user who should reimburse this expense
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "for_user_id", nullable = false)
    private User forUser;

    /**
     * Whether this expense has been reimbursed
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isPaid = false;
}
