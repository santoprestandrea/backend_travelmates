package com.santoprestandrea_s00007624.backend_travelmates.repository;

import com.santoprestandrea_s00007624.backend_travelmates.entity.Document;
import com.santoprestandrea_s00007624.backend_travelmates.entity.DocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORY FOR DOCUMENT
 *
 * Manages database access for trip documents.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Find all documents for a trip, ordered by upload date (newest first)
     */
    List<Document> findByTrip_IdOrderByUploadDateDesc(Long tripId);

    /**
     * Find documents by trip and category
     */
    List<Document> findByTrip_IdAndCategoryOrderByUploadDateDesc(Long tripId, DocumentCategory category);

    /**
     * Find documents uploaded by a specific user
     */
    List<Document> findByUploadedBy_IdOrderByUploadDateDesc(Long userId);

    /**
     * Find documents by trip and uploader
     */
    List<Document> findByTrip_IdAndUploadedBy_IdOrderByUploadDateDesc(Long tripId, Long userId);

    /**
     * Count documents for a trip
     */
    long countByTrip_Id(Long tripId);

    /**
     * Count documents by category for a trip
     */
    long countByTrip_IdAndCategory(Long tripId, DocumentCategory category);

    /**
     * Calculate total file size for a trip
     */
    @Query("SELECT COALESCE(SUM(d.fileSize), 0) FROM Document d WHERE d.trip.id = :tripId")
    Long calculateTotalFileSizeByTripId(@Param("tripId") Long tripId);

    /**
     * Search documents by file name
     */
    @Query("SELECT d FROM Document d WHERE d.trip.id = :tripId AND LOWER(d.fileName) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY d.uploadDate DESC")
    List<Document> searchByFileName(@Param("tripId") Long tripId, @Param("keyword") String keyword);
}
