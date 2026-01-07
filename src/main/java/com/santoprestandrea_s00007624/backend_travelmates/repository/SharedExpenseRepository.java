package com.santoprestandrea_s00007624.backend_travelmates.repository;

import com.santoprestandrea_s00007624.backend_travelmates.entity.SharedExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORY FOR SHARED EXPENSE
 *
 * Specialized queries for shared expenses with splits.
 */
@Repository
public interface SharedExpenseRepository extends JpaRepository<SharedExpense, Long> {

    /**
     * Find all shared expenses for a trip
     */
    List<SharedExpense> findByTrip_Id(Long tripId);

    /**
     * Find shared expenses where a user is involved (either paid or owes)
     */
    @Query("SELECT DISTINCT se FROM SharedExpense se " +
            "LEFT JOIN se.splits s " +
            "WHERE se.trip.id = :tripId AND (se.paidBy.id = :userId OR s.user.id = :userId)")
    List<SharedExpense> findByTripAndUserInvolved(@Param("tripId") Long tripId, @Param("userId") Long userId);
}
