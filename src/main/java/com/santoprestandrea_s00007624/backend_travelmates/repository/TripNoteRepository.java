package com.santoprestandrea_s00007624.backend_travelmates.repository;

import com.santoprestandrea_s00007624.backend_travelmates.entity.TripNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORY FOR TRIP NOTE
 *
 * Manages database access for trip notes.
 */
@Repository
public interface TripNoteRepository extends JpaRepository<TripNote, Long> {

    /**
     * Find all notes for a trip with pagination
     * Ordered by: pinned first, then newest first
     */
    Page<TripNote> findByTrip_IdOrderByIsPinnedDescCreatedAtDesc(Long tripId, Pageable pageable);

    /**
     * Find all notes for a trip (no pagination)
     */
    List<TripNote> findByTrip_IdOrderByIsPinnedDescCreatedAtDesc(Long tripId);

    /**
     * Find pinned notes only
     */
    List<TripNote> findByTrip_IdAndIsPinnedTrueOrderByCreatedAtDesc(Long tripId);

    /**
     * Find notes by author
     */
    List<TripNote> findByAuthor_IdOrderByCreatedAtDesc(Long authorId);

    /**
     * Count notes for a trip
     */
    long countByTrip_Id(Long tripId);
}
