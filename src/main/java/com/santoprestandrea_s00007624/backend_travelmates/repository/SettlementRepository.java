package com.santoprestandrea_s00007624.backend_travelmates.repository;

import com.santoprestandrea_s00007624.backend_travelmates.entity.Settlement;
import com.santoprestandrea_s00007624.backend_travelmates.entity.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * REPOSITORY FOR SETTLEMENT
 *
 * Manages database access for settlements.
 */
@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    /**
     * Find all settlements for a trip (ordered by creation date)
     */
    List<Settlement> findByTrip_IdOrderByCreatedAtDesc(Long tripId);

    /**
     * Find settlements by trip and status
     */
    List<Settlement> findByTrip_IdAndStatusOrderByCreatedAtDesc(Long tripId, SettlementStatus status);

    /**
     * Find settlements where user owes money
     */
    List<Settlement> findByFromUser_IdAndStatusOrderByCreatedAtDesc(Long userId, SettlementStatus status);

    /**
     * Find settlements where user should receive money
     */
    List<Settlement> findByToUser_IdAndStatusOrderByCreatedAtDesc(Long userId, SettlementStatus status);

    /**
     * Find settlements involving a user (either from or to)
     */
    @Query("SELECT s FROM Settlement s WHERE s.trip.id = :tripId AND (s.fromUser.id = :userId OR s.toUser.id = :userId) ORDER BY s.createdAt DESC")
    List<Settlement> findByTripAndUser(@Param("tripId") Long tripId, @Param("userId") Long userId);

    /**
     * Calculate total pending settlements for a user in a trip
     */
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Settlement s WHERE s.trip.id = :tripId AND s.fromUser.id = :userId AND s.status = 'PENDING'")
    BigDecimal calculateTotalOwed(@Param("tripId") Long tripId, @Param("userId") Long userId);

    /**
     * Calculate total pending settlements a user should receive
     */
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Settlement s WHERE s.trip.id = :tripId AND s.toUser.id = :userId AND s.status = 'PENDING'")
    BigDecimal calculateTotalToReceive(@Param("tripId") Long tripId, @Param("userId") Long userId);
}