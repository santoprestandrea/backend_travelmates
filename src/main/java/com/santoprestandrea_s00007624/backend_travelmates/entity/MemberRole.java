package com.santoprestandrea_s00007624.backend_travelmates.entity;

/**
 * ROLE OF A MEMBER IN THE TRIP
 *
 * Defines the permissions of a user within a specific trip.
 * NOTE: This is different from UserRole (ADMIN/ORGANIZER/TRAVELER)!
 *
 * Example:
 * - Mario has UserRole = TRAVELER (global role in the app)
 * - But in the trip "Paris 2025" he has MemberRole = ORGANIZER (organizes that
 * trip)
 */
public enum MemberRole {

    /**
     * ORGANIZER - Trip organizer
     *
     * PERMISSIONS:
     * - Modify trip title, description, dates
     * - Invite/remove members
     * - Delete the trip
     * - Modify roles of other members
     * - Manage activities, expenses, documents
     *
     * NOTE: There can be MULTIPLE organizers in a trip!
     */
    ORGANIZER,

    /**
     * PARTICIPANT - Simple participant
     *
     * PERMISSIONS:
     * - View trip details
     * - Add personal expenses
     * - Comment on activities
     * - Upload documents
     * - CANNOT modify the trip
     * - CANNOT invite other members
     * - CANNOT delete the trip
     */
    PARTICIPANT
}
