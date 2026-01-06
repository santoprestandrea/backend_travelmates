package com.santoprestandrea_s00007624.backend_travelmates.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * TRIP_MEMBER ENTITY - Trip participants
 *
 * This is a JUNCTION TABLE that connects:
 * - User (who participates)
 * - Trip (which trip)
 *
 * WHY DO WE NEED A SEPARATE TABLE?
 * Because we need to store EXTRA INFORMATION about the relationship:
 * - Role in the trip (ORGANIZER or PARTICIPANT)
 * - When they joined
 * - Invitation status
 *
 * DATABASE EXAMPLE:
 * ┌────┬─────────┬─────────┬──────────────┬────────────┐
 * │ id │ user_id │ trip_id │     role     │ joined_at  │
 * ├────┼─────────┼─────────┼──────────────┼────────────┤
 * │ 1  │    5    │    1    │  ORGANIZER   │ 2025-01-01 │ Mario organizes "Paris 2025"
 * │ 2  │    8    │    1    │ PARTICIPANT  │ 2025-01-02 │ Luca participates in "Paris 2025"
 * │ 3  │    5    │    2    │ PARTICIPANT  │ 2025-01-10 │ Mario participates in "Rome 2025"
 * └────┴─────────┴─────────┴──────────────┴────────────┘
 */
@Entity
@Table(name = "trip_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripMember {

    // ===== IDENTIFICATION =====

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== RELATIONSHIPS =====

    /**
     * USER WHO PARTICIPATES
     *
     * MANY-TO-ONE relationship:
     * - Many members can refer to the same user
     * - One member belongs to one user
     *
     * fetch = LAZY:
     * - Doesn't load the user until needed (optimization)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * TRIP THEY PARTICIPATE IN
     *
     * MANY-TO-ONE relationship:
     * - Many members belong to the same trip
     * - One member belongs to one trip
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    // ===== TRIP ROLE =====

    /**
     * ROLE: ORGANIZER or PARTICIPANT
     *
     * Determines the user's permissions on this specific trip.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MemberRole role = MemberRole.PARTICIPANT;

    // ===== TIMESTAMPS =====

    /**
     * WHEN THEY JOINED THE TRIP
     *
     * Automatically set on creation.
     */
    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    // ===== INVITATION STATUS =====

    /**
     * INVITATION STATUS
     *
     * Possible values:
     * - PENDING: Invited, but hasn't accepted yet
     * - ACCEPTED: Has accepted the invitation
     * - DECLINED: Has declined the invitation
     *
     * Default: ACCEPTED (when the organizer creates the trip)
     */
    @Column(name = "invitation_status", length = 20)
    @Builder.Default
    private String invitationStatus = "ACCEPTED";

    // ===== HELPER METHODS =====

    /**
     * CHECK IF IS ORGANIZER
     */
    public boolean isOrganizer() {
        return role == MemberRole.ORGANIZER;
    }

    /**
     * CHECK IF IS REGULAR PARTICIPANT
     */
    public boolean isParticipant() {
        return role == MemberRole.PARTICIPANT;
    }

    /**
     * CHECK IF HAS ACCEPTED THE INVITATION
     */
    public boolean hasAcceptedInvitation() {
        return "ACCEPTED".equals(invitationStatus);
    }
}
