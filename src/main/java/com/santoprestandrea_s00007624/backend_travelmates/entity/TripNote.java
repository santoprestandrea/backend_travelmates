package com.santoprestandrea_s00007624.backend_travelmates.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * TRIP NOTE ENTITY - Represents a note/message in a trip
 *
 * Trip notes are messages shared among trip members.
 * Can be used for announcements, reminders, or general chat.
 */
@Entity
@Table(name = "trip_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * TRIP ASSOCIATION
     * Each note belongs to a specific trip
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    /**
     * AUTHOR
     * User who created this note
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * CONTENT
     */
    @Column(nullable = false, length = 2000)
    private String content;

    /**
     * PINNED STATUS
     * Pinned notes appear at the top
     */
    @Column(name = "is_pinned")
    private Boolean isPinned;

    /**
     * TIMESTAMPS
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
