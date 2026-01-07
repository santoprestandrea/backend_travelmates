package com.santoprestandrea_s00007624.backend_travelmates.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * SHARED EXPENSE
 *
 * An expense that is split among multiple trip members.
 *
 * EXAMPLE:
 * - Dinner at restaurant: €120
 * - Paid by: Mario
 * - Split among: Mario, Luca, Anna, Sofia
 * - Split type: EQUAL → €30 each
 *
 * The actual split details are in ExpenseSplit entities.
 */
@Entity
@Table(name = "shared_expenses")
@DiscriminatorValue("SHARED")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedExpense extends Expense {

    /**
     * How the expense should be split
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SplitType splitType;

    /**
     * Individual splits for each participant
     * CascadeType.ALL: when we delete the expense, delete all splits
     * orphanRemoval: if a split is removed from the list, delete it from DB
     */
    @OneToMany(mappedBy = "sharedExpense", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExpenseSplit> splits = new ArrayList<>();

    /**
     * Helper method to add a split
     */
    public void addSplit(ExpenseSplit split) {
        splits.add(split);
        split.setSharedExpense(this);
    }

    /**
     * Helper method to remove a split
     */
    public void removeSplit(ExpenseSplit split) {
        splits.remove(split);
        split.setSharedExpense(null);
    }
}
