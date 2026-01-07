package com.santoprestandrea_s00007624.backend_travelmates.repository;

import com.santoprestandrea_s00007624.backend_travelmates.entity.PersonalExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORY FOR PERSONAL EXPENSE
 *
 * One-to-one expenses between users.
 */
@Repository
public interface PersonalExpenseRepository extends JpaRepository<PersonalExpense, Long> {

    /**
     * Find all personal expenses for a trip
     */
    List<PersonalExpense> findByTrip_Id(Long tripId);

    /**
     * Find unpaid personal expenses where user owes money
     */
    List<PersonalExpense> findByTrip_IdAndForUser_IdAndIsPaidFalse(Long tripId, Long userId);

    /**
     * Find personal expenses paid by a user (where they are owed money)
     */
    List<PersonalExpense> findByTrip_IdAndPaidBy_Id(Long tripId, Long userId);

    /**
     * Find all unpaid personal expenses in a trip
     */
    List<PersonalExpense> findByTrip_IdAndIsPaidFalse(Long tripId);
}
