package com.santoprestandrea_s00007624.backend_travelmates.repository;

import com.santoprestandrea_s00007624.backend_travelmates.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * REPOSITORY FOR EXPENSE SPLIT
 *
 * Manages individual splits of shared expenses.
 */
@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {

    /**
     * Find all splits for a shared expense
     */
    List<ExpenseSplit> findBySharedExpense_Id(Long sharedExpenseId);

    /**
     * Find unpaid splits for a user in a trip
     */
    @Query("SELECT s FROM ExpenseSplit s WHERE s.sharedExpense.trip.id = :tripId AND s.user.id = :userId AND s.isPaid = false")
    List<ExpenseSplit> findUnpaidSplitsByTripAndUser(@Param("tripId") Long tripId, @Param("userId") Long userId);

    /**
     * Calculate total amount a user owes in a trip (from splits)
     */
    @Query("SELECT SUM(s.amount) FROM ExpenseSplit s WHERE s.sharedExpense.trip.id = :tripId AND s.user.id = :userId AND s.isPaid = false")
    BigDecimal getTotalOwedByUser(@Param("tripId") Long tripId, @Param("userId") Long userId);

    /**
     * Find all splits for a user across all their trips
     */
    List<ExpenseSplit> findByUser_Id(Long userId);
}
