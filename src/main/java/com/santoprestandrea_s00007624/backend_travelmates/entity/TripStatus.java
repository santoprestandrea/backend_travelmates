package com.santoprestandrea_s00007624.backend_travelmates.entity;


/**
 * TRIP STATUSES
 *
 * Represents the lifecycle of a trip in TravelMates.
 */
public enum TripStatus {

    /**
     * PLANNING - Trip in planning phase
     * - Still in organizational phase
     * - Dates might change
     * - Members can be added
     */
    PLANNING,

    /**
     * ACTIVE - Ongoing trip
     * - The trip has started (startDate <= today)
     * - Expenses are being added
     * - Activities in progress
     */
    ACTIVE,

    /**
     * COMPLETED - Completed trip
     * - The trip has ended (endDate < today)
     * - Remains visible for consultation
     * - Final expenses calculated
     */
    COMPLETED,

    /**
     * CANCELLED - Cancelled trip
     * - The trip will no longer happen
     * - Remains in database for history
     * - No further modifications allowed
     */
    CANCELLED
}