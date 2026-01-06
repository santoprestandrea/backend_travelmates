package com.santoprestandrea_s00007624.backend_travelmates.repository;


import com.santoprestandrea_s00007624.backend_travelmates.entity.Trip;
import com.santoprestandrea_s00007624.backend_travelmates.entity.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * REPOSITORY FOR TRIP
 *
 * Manages database access for trips.
 * Spring Data JPA automatically generates implementations!
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // ===== AUTOMATIC QUERIES (Spring generates SQL) =====

    /**
     * FIND ALL TRIPS OF A USER
     *
     * Automatically generated query:
     * SELECT t.* FROM trips t
     * JOIN trip_members tm ON t.id = tm.trip_id
     * WHERE tm.user_id = ?
     *
     * Usage: tripRepository.findByMembers_User_Id(5L);
     */
    List<Trip> findByMembers_User_Id(Long userId);

    /**
     * FIND TRIPS BY STATUS
     *
     * Query: SELECT * FROM trips WHERE status = ?
     *
     * Usage: tripRepository.findByStatus(TripStatus.ACTIVE);
     */
    List<Trip> findByStatus(TripStatus status);

    /**
     * FIND TRIPS BY DESTINATION
     *
     * Case-insensitive LIKE
     * Query: SELECT * FROM trips WHERE LOWER(destination) LIKE LOWER(?)
     *
     * Usage: tripRepository.findByDestinationContainingIgnoreCase("paris");
     */
    List<Trip> findByDestinationContainingIgnoreCase(String destination);

    /**
     * FIND TRIPS IN A DATE RANGE
     *
     * Trips that:
     * - Start after fromDate
     * - End before toDate
     *
     * Usage: tripRepository.findByStartDateAfterAndEndDateBefore(today,
     * inOneMonth);
     */
    List<Trip> findByStartDateAfterAndEndDateBefore(LocalDate fromDate, LocalDate toDate);

    // ===== CUSTOM QUERIES (with @Query) =====

    /**
     * FIND USER'S TRIPS WITH SPECIFIC STATUS
     *
     * Custom JPQL Query.
     *
     * Example: All ACTIVE trips of Mario
     * tripRepository.findByUserIdAndStatus(5L, TripStatus.ACTIVE);
     */
    @Query("SELECT t FROM Trip t JOIN t.members m WHERE m.user.id = :userId AND t.status = :status")
    List<Trip> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TripStatus status);

    /**
     * FIND TRIPS WHERE USER IS ORGANIZER
     *
     * JPQL Query: search only trips where user has ORGANIZER role
     */
    @Query("SELECT t FROM Trip t JOIN t.members m WHERE m.user.id = :userId AND m.role = 'ORGANIZER'")
    List<Trip> findTripsWhereUserIsOrganizer(@Param("userId") Long userId);

    /**
     * FIND ACTIVE TRIPS (happening NOW)
     *
     * Conditions:
     * - status = ACTIVE
     * - startDate <= today <= endDate
     */
    @Query("SELECT t FROM Trip t WHERE t.status = 'ACTIVE' AND :today BETWEEN t.startDate AND t.endDate")
    List<Trip> findActiveTripsToday(@Param("today") LocalDate today);

    /**
     * COUNT TRIPS BY USER
     *
     * Returns the total number of trips for a user.
     */
    @Query("SELECT COUNT(t) FROM Trip t JOIN t.members m WHERE m.user.id = :userId")
    Long countTripsByUserId(@Param("userId") Long userId);

    /**
     * SEARCH TRIPS (title OR destination)
     *
     * Case-insensitive search in title and destination.
     *
     * Example: tripRepository.searchTrips("paris");
     * Finds: "Vacation in Paris", "Weekend Paris", etc.
     */
    @Query("SELECT t FROM Trip t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.destination) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Trip> searchTrips(@Param("keyword") String keyword);
}
