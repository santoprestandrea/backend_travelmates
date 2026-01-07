package com.santoprestandrea_s00007624.backend_travelmates.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ACTIVITY ENTITY - Represents a planned activity in a trip
 *
 * Activities are events, excursions, or scheduled tasks during a trip.
 * Examples: Museum visit, restaurant reservation, flight booking, hiking trip.
 */
@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * TRIP ASSOCIATION
     * Each activity belongs to a specific trip
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    /**
     * BASIC INFORMATION
     */
    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    /**
     * SCHEDULING
     */
    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    /**
     * Duration in minutes (e.g., 120 = 2 hours)
     */
    @Column
    private Integer duration;

    /**
     * LOCATION
     */
    @Column(length = 200)
    private String location;

    /**
     * CATEGORY
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityCategory category;

    /**
     * COST INFORMATION
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(length = 3)
    private String currency;

    /**
     * BOOKING
     */
    @Column(name = "booking_url", length = 500)
    private String bookingUrl;

    @Column(name = "booking_reference", length = 100)
    private String bookingReference;

    /**
     * STATUS
     */
    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    @Column(name = "is_cancelled")
    private Boolean isCancelled;

    /**
     * CREATOR
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * TIMESTAMPS
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * NOTES
     */
    @Column(length = 1000)
    private String notes;
}
