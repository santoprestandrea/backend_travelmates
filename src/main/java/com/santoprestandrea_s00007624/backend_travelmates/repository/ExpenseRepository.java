package com.santoprestandrea_s00007624.backend_travelmates.repository;

import com.santoprestandrea_s00007624.backend_travelmates.entity.Expense;
import com.santoprestandrea_s00007624.backend_travelmates.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REPOSITORY FOR EXPENSE (BASE)
 *
 * Manages database access for all expense types.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Find all expenses for a trip, ordered by date (newest first)
     */
    List<Expense> findByTrip_IdOrderByDateDesc(Long tripId);

    /**
     * Find all expenses for a trip by category
     */
    List<Expense> findByTrip_IdAndCategory(Long tripId, ExpenseCategory category);

    /**
     * Find expenses paid by a specific user in a trip
     */
    List<Expense> findByTrip_IdAndPaidBy_Id(Long tripId, Long userId);

    /**
     * Find expenses within a date range
     */
    @Query("SELECT e FROM Expense e WHERE e.trip.id = :tripId AND e.date BETWEEN :startDate AND :endDate ORDER BY e.date DESC")
    List<Expense> findByTripAndDateRange(
            @Param("tripId") Long tripId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Calculate total expenses for a trip
     */
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.trip.id = :tripId")
    BigDecimal getTotalExpenses(@Param("tripId") Long tripId);

    /**
     * Calculate total by category (for statistics)
     */
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.trip.id = :tripId GROUP BY e.category")
    List<Object[]> getTotalByCategory(@Param("tripId") Long tripId);

    /**
     * Find users who paid the most (top payers)
     */
    @Query("SELECT e.paidBy, SUM(e.amount) FROM Expense e WHERE e.trip.id = :tripId GROUP BY e.paidBy ORDER BY SUM(e.amount) DESC")
    List<Object[]> getTopPayers(@Param("tripId") Long tripId);

    /**
     * Count expenses in a trip
     */
    Long countByTrip_Id(Long tripId);

    /**
     * Calculate average expense amount
     */
    @Query("SELECT AVG(e.amount) FROM Expense e WHERE e.trip.id = :tripId")
    BigDecimal getAverageExpense(@Param("tripId") Long tripId);
}
