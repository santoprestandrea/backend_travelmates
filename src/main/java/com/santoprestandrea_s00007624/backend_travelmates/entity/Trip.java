package com.santoprestandrea_s00007624.backend_travelmates.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TRIP ENTITY - Represents a trip
 *
 * This is the central table of the application.
 * Each trip can have:
 * - Multiple participants (relationship with TripMember)
 * - Multiple expenses (relationship with Expense - PHASE 2)
 * - Multiple activities (relationship with Activity - PHASE 3)
 * - Multiple documents (relationship with Document - PHASE 4)
 */
@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    // ===== IDENTIFICATION =====

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== BASIC INFORMATION =====

    /**
     * Trip title (e.g., "Paris Vacation 2025")
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * Detailed trip description
     */
    @Column(length = 1000)
    private String description;

    /**
     * Main destination (e.g., "Paris, France")
     */
    @Column(nullable = false, length = 100)
    private String destination;

    // ===== DATES =====

    /**
     * Trip start date
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Trip end date
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // ===== BUDGET =====

    /**
     * Total trip budget
     * Uses BigDecimal for precision in monetary calculations
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal budget;

    /**
     * Budget currency (e.g., "EUR", "USD")
     * Default: EUR
     */
    @Column(length = 3)
    @Builder.Default
    private String currency = "EUR";

    // ===== IMAGE =====

    /**
     * Cover image URL
     * E.g., destination photo
     */
    @Column(name = "cover_image_url")
    private String coverImageUrl;

    // ===== STATUS =====

    /**
     * Current trip status
     * Values: PLANNING, ACTIVE, COMPLETED, CANCELLED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TripStatus status = TripStatus.PLANNING;

    // ===== AUTOMATIC TIMESTAMPS =====

    /**
     * Trip creation date (set automatically)
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last update date (updated automatically)
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== RELATIONSHIPS =====

    /**
     * TRIP MEMBERS
     *
     * ONE-TO-MANY relationship:
     * - One trip has MANY members
     * - One member belongs to ONE trip
     *
     * orphanRemoval = true:
     * - If you remove a member from the list, it is DELETED from the database
     *
     * cascade = ALL:
     * - If you save a Trip, it also saves the members
     * - If you delete a Trip, it also deletes the members
     *
     * mappedBy = "trip":
     * - The "trip" field in TripMember manages the relationship
     */
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TripMember> members = new ArrayList<>();

    // ===== HELPER METHODS =====

    /**
     * ADD MEMBER TO TRIP
     *
     * Keeps both sides of the relationship synchronized:
     * - Adds the member to the trip's list
     * - Sets the trip in the member
     */
    public void addMember(TripMember member) {
        members.add(member);
        member.setTrip(this);
    }

    /**
     * REMOVE MEMBER FROM TRIP
     */
    public void removeMember(TripMember member) {
        members.remove(member);
        member.setTrip(null);
    }

    /**
     * CHECK IF THE TRIP IS ACTIVE
     *
     * A trip is active if:
     * - The status is ACTIVE
     * - The current date is between startDate and endDate
     */
    public boolean isActive() {
        if (status != TripStatus.ACTIVE) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    /**
     * CALCULATE TRIP DURATION IN DAYS
     */
    public long getDurationInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}
