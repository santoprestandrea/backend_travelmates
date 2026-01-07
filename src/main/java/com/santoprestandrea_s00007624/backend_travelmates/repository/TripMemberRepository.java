package com.santoprestandrea_s00007624.backend_travelmates.repository;

import com.santoprestandrea_s00007624.backend_travelmates.entity.MemberRole;
import com.santoprestandrea_s00007624.backend_travelmates.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY FOR TRIP_MEMBER
 *
 * Manages trip participants.
 */
@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, Long> {

    // ===== AUTOMATIC QUERIES =====

    /**
     * FIND ALL MEMBERS OF A TRIP
     *
     * Query: SELECT * FROM trip_members WHERE trip_id = ?
     */
    List<TripMember> findByTrip_Id(Long tripId);

    /**
     * FIND ALL TRIPS OF A USER
     *
     * Query: SELECT * FROM trip_members WHERE user_id = ?
     */
    List<TripMember> findByUser_Id(Long userId);

    /**
     * FIND SPECIFIC MEMBER (user in specific trip)
     *
     * Query: SELECT * FROM trip_members WHERE user_id = ? AND trip_id = ?
     *
     * IMPORTANT: Returns Optional because it might not exist!
     */
    Optional<TripMember> findByUser_IdAndTrip_Id(Long userId, Long tripId);

    /**
     * FIND SPECIFIC MEMBER (alternative parameter order)
     *
     * Query: SELECT * FROM trip_members WHERE trip_id = ? AND user_id = ?
     *
     * IMPORTANT: Returns Optional because it might not exist!
     */
    Optional<TripMember> findByTrip_IdAndUser_Id(Long tripId, Long userId);

    /**
     * CHECK IF USER IS MEMBER OF A TRIP
     *
     * Query: SELECT COUNT(*) > 0 FROM trip_members WHERE user_id = ? AND trip_id =
     * ?
     */
    boolean existsByUser_IdAndTrip_Id(Long userId, Long tripId);

    /**
     * FIND ORGANIZER OF A TRIP
     *
     * Query: SELECT * FROM trip_members WHERE trip_id = ? AND role = 'ORGANIZER'
     */
    List<TripMember> findByTrip_IdAndRole(Long tripId, MemberRole role);

    /**
     * COUNT MEMBERS OF A TRIP
     *
     * Query: SELECT COUNT(*) FROM trip_members WHERE trip_id = ?
     */
    Long countByTrip_Id(Long tripId);

    // ===== CUSTOM QUERIES =====

    /**
     * FIND PENDING INVITATIONS OF A USER
     *
     * All trips to which the user is invited but has not yet accepted.
     */
    @Query("SELECT tm FROM TripMember tm WHERE tm.user.id = :userId AND tm.invitationStatus = 'PENDING'")
    List<TripMember> findPendingInvitationsByUserId(@Param("userId") Long userId);

    /**
     * FIND MEMBERS WHO HAVE ACCEPTED
     *
     * Only members with invitationStatus = 'ACCEPTED'
     */
    @Query("SELECT tm FROM TripMember tm WHERE tm.trip.id = :tripId AND tm.invitationStatus = 'ACCEPTED'")
    List<TripMember> findAcceptedMembersByTripId(@Param("tripId") Long tripId);

    /**
     * DELETE MEMBER FROM TRIP
     *
     * Custom delete query.
     */
    void deleteByUser_IdAndTrip_Id(Long userId, Long tripId);
}
