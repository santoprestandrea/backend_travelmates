package com.santoprestandrea_s00007624.backend_travelmates.repository;

import com.santoprestandrea_s00007624.backend_travelmates.entity.Activity;
import com.santoprestandrea_s00007624.backend_travelmates.entity.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REPOSITORY FOR ACTIVITY
 *
 * Manages database access for trip activities.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * Find all activities for a trip, ordered by scheduled date (oldest first)
     */
    List<Activity> findByTrip_IdOrderByScheduledDateAsc(Long tripId);

    /**
     * Find activities by trip and category
     */
    List<Activity> findByTrip_IdAndCategoryOrderByScheduledDateAsc(Long tripId, ActivityCategory category);

    /**
     * Find activities scheduled between two dates
     */
    @Query("SELECT a FROM Activity a WHERE a.trip.id = :tripId AND a.scheduledDate BETWEEN :startDate AND :endDate ORDER BY a.scheduledDate ASC")
    List<Activity> findByTripIdAndScheduledDateBetween(
            @Param("tripId") Long tripId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find upcoming activities (from now onwards)
     */
    @Query("SELECT a FROM Activity a WHERE a.trip.id = :tripId AND a.scheduledDate >= :now AND a.isCancelled = false ORDER BY a.scheduledDate ASC")
    List<Activity> findUpcomingActivities(@Param("tripId") Long tripId, @Param("now") LocalDateTime now);

    /**
     * Find confirmed activities for a trip
     */
    List<Activity> findByTrip_IdAndIsConfirmedTrueOrderByScheduledDateAsc(Long tripId);

    /**
     * Count activities for a trip
     */
    long countByTrip_Id(Long tripId);

    /**
     * Count upcoming activities
     */
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.trip.id = :tripId AND a.scheduledDate >= :now AND a.isCancelled = false")
    long countUpcomingActivities(@Param("tripId") Long tripId, @Param("now") LocalDateTime now);

    /**
     * Find activities created by a specific user
     */
    List<Activity> findByCreatedBy_IdOrderByScheduledDateDesc(Long userId);

    /**
     * Find all non-cancelled activities for a trip
     */
    @Query("SELECT a FROM Activity a WHERE a.trip.id = :tripId AND a.isCancelled = false ORDER BY a.scheduledDate ASC")
    List<Activity> findActiveActivitiesByTripId(@Param("tripId") Long tripId);

    /**
     * Calculate total cost of activities for a trip
     */
    @Query("SELECT COALESCE(SUM(a.cost), 0) FROM Activity a WHERE a.trip.id = :tripId AND a.isCancelled = false")
    java.math.BigDecimal calculateTotalCostByTripId(@Param("tripId") Long tripId);
}
