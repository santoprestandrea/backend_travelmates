package com.santoprestandrea_s00007624.backend_travelmates.service;



import com.santoprestandrea_s00007624.backend_travelmates.dto.request.InviteMemberRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateTripRequest;
import com.santoprestandrea_s00007624.backend_travelmates.entity.*;
import com.santoprestandrea_s00007624.backend_travelmates.exception.ResourceNotFoundException;
import com.santoprestandrea_s00007624.backend_travelmates.exception.UnauthorizedException;
import com.santoprestandrea_s00007624.backend_travelmates.repository.TripMemberRepository;
import com.santoprestandrea_s00007624.backend_travelmates.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * SERVICE FOR TRIPS
 *
 * Manages all business logic for trips:
 * - Create/update/delete trips
 * - Manage members (invitations, removals, role changes)
 * - Authorization checks
 * - Business logic validations
 */
@Service
@Transactional
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripMemberRepository tripMemberRepository;

    @Autowired
    private UserService userService;

    // ===== CREATE =====

    /**
     * CREATE NEW TRIP
     *
     * LOGIC:
     * 1. Validate dates (endDate >= startDate)
     * 2. Create trip with PLANNING status
     * 3. Add creator as ORGANIZER
     * 4. Save everything to database
     *
     * @param trip      The trip to create (already mapped from DTO)
     * @param creatorId ID of the user creating the trip
     * @return The created trip
     */
    public Trip createTrip(Trip trip, Long creatorId) {
        // Validate dates
        validateTripDates(trip.getStartDate(), trip.getEndDate());

        // Save trip
        Trip savedTrip = tripRepository.save(trip);

        // Add creator as ORGANIZER
        User creator = userService.findByIdOrThrow(creatorId);
        TripMember organizerMember = TripMember.builder()
                .trip(savedTrip)
                .user(creator)
                .role(MemberRole.ORGANIZER)
                .invitationStatus("ACCEPTED")
                .build();

        savedTrip.addMember(organizerMember);
        tripMemberRepository.save(organizerMember);

        return savedTrip;
    }

    // ===== READ =====

    /**
     * FIND TRIP BY ID
     *
     * Throws exception if not found.
     */
    public Trip findByIdOrThrow(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));
    }

    /**
     * FIND ALL TRIPS FOR A USER
     */
    public List<Trip> findTripsByUserId(Long userId) {
        return tripRepository.findByMembers_User_Id(userId);
    }

    /**
     * FIND TRIPS BY STATUS
     */
    public List<Trip> findTripsByStatus(TripStatus status) {
        return tripRepository.findByStatus(status);
    }

    /**
     * FIND TRIPS WHERE USER IS ORGANIZER
     */
    public List<Trip> findTripsWhereUserIsOrganizer(Long userId) {
        return tripRepository.findTripsWhereUserIsOrganizer(userId);
    }

    /**
     * SEARCH TRIPS BY KEYWORD
     */
    public List<Trip> searchTrips(String keyword) {
        return tripRepository.searchTrips(keyword);
    }

    /**
     * COUNT USER'S TRIPS
     */
    public Long countTripsByUserId(Long userId) {
        return tripRepository.countTripsByUserId(userId);
    }

    // ===== UPDATE =====

    /**
     * UPDATE TRIP
     *
     * CHECKS:
     * 1. Trip exists
     * 2. User is ORGANIZER of the trip
     * 3. Dates are valid
     *
     * @param tripId  ID of the trip to update
     * @param request Data to update
     * @param userId  ID of the user requesting the update
     * @return The updated trip
     */
    public Trip updateTrip(Long tripId, UpdateTripRequest request, Long userId) {
        // Find trip
        Trip trip = findByIdOrThrow(tripId);

        // Verify user is ORGANIZER
        checkUserIsOrganizer(trip, userId);

        // Validate dates if provided
        LocalDate newStartDate = request.getStartDate() != null ? request.getStartDate() : trip.getStartDate();
        LocalDate newEndDate = request.getEndDate() != null ? request.getEndDate() : trip.getEndDate();
        validateTripDates(newStartDate, newEndDate);

        // Update fields (handled in mapper)
        // Actually here we don't use mapper, we do it manually
        if (request.getTitle() != null)
            trip.setTitle(request.getTitle());
        if (request.getDescription() != null)
            trip.setDescription(request.getDescription());
        if (request.getDestination() != null)
            trip.setDestination(request.getDestination());
        if (request.getStartDate() != null)
            trip.setStartDate(request.getStartDate());
        if (request.getEndDate() != null)
            trip.setEndDate(request.getEndDate());
        if (request.getBudget() != null)
            trip.setBudget(request.getBudget());
        if (request.getCurrency() != null)
            trip.setCurrency(request.getCurrency());
        if (request.getCoverImageUrl() != null)
            trip.setCoverImageUrl(request.getCoverImageUrl());
        if (request.getStatus() != null)
            trip.setStatus(request.getStatus());

        return tripRepository.save(trip);
    }

    /**
     * CHANGE TRIP STATUS
     *
     * Only ORGANIZER can change status.
     */
    public Trip updateTripStatus(Long tripId, TripStatus newStatus, Long userId) {
        Trip trip = findByIdOrThrow(tripId);
        checkUserIsOrganizer(trip, userId);

        trip.setStatus(newStatus);
        return tripRepository.save(trip);
    }

    // ===== DELETE =====

    /**
     * DELETE TRIP
     *
     * Only ORGANIZER can delete.
     * Also deletes all members (cascade).
     */
    public void deleteTrip(Long tripId, Long userId) {
        Trip trip = findByIdOrThrow(tripId);
        checkUserIsOrganizer(trip, userId);

        tripRepository.delete(trip);
    }

    // ===== MEMBER MANAGEMENT =====

    /**
     * INVITE MEMBER TO TRIP
     *
     * LOGIC:
     * 1. Only ORGANIZER can invite
     * 2. User exists
     * 3. User is not already a member
     * 4. Create TripMember with invitationStatus = PENDING
     *
     * @param tripId    Trip ID
     * @param request   Email and role of new member
     * @param inviterId ID of the inviter
     * @return The created TripMember
     */
    public TripMember inviteMember(Long tripId, InviteMemberRequest request, Long inviterId) {
        // Check permissions
        Trip trip = findByIdOrThrow(tripId);
        checkUserIsOrganizer(trip, inviterId);

        // Find user to invite
        User userToInvite = userService.findByEmail(request.getUserEmail())
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found with email: " + request.getUserEmail()));

        // Verify not already a member
        if (tripMemberRepository.existsByUser_IdAndTrip_Id(userToInvite.getId(), tripId)) {
            throw new IllegalArgumentException("User is already a member of this trip");
        }

        // Create member
        TripMember newMember = TripMember.builder()
                .trip(trip)
                .user(userToInvite)
                .role(request.getRole())
                .invitationStatus("PENDING")
                .build();

        return tripMemberRepository.save(newMember);
    }

    /**
     * ACCEPT INVITATION
     *
     * The invited user accepts to join the trip.
     */
    public TripMember acceptInvitation(Long tripId, Long userId) {
        TripMember member = tripMemberRepository.findByUser_IdAndTrip_Id(userId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (!"PENDING".equals(member.getInvitationStatus())) {
            throw new IllegalArgumentException("Invitation already handled");
        }

        member.setInvitationStatus("ACCEPTED");
        return tripMemberRepository.save(member);
    }

    /**
     * DECLINE INVITATION
     */
    public void declineInvitation(Long tripId, Long userId) {
        TripMember member = tripMemberRepository.findByUser_IdAndTrip_Id(userId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        member.setInvitationStatus("DECLINED");
        tripMemberRepository.save(member);
    }

    /**
     * REMOVE MEMBER FROM TRIP
     *
     * Only ORGANIZER can remove members.
     * Cannot remove themselves if they are the only ORGANIZER.
     */
    public void removeMember(Long tripId, Long memberUserId, Long requesterId) {
        Trip trip = findByIdOrThrow(tripId);
        checkUserIsOrganizer(trip, requesterId);

        // Verify member exists
        TripMember member = tripMemberRepository.findByUser_IdAndTrip_Id(memberUserId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in this trip"));

        // Prevent removal of last ORGANIZER
        if (member.getRole() == MemberRole.ORGANIZER) {
            long organizerCount = tripMemberRepository.findByTrip_IdAndRole(tripId, MemberRole.ORGANIZER).size();
            if (organizerCount <= 1) {
                throw new IllegalArgumentException("Cannot remove the only organizer of the trip");
            }
        }

        tripMemberRepository.delete(member);
    }

    /**
     * CHANGE MEMBER ROLE
     *
     * Only ORGANIZER can change roles.
     */
    public TripMember updateMemberRole(Long tripId, Long memberUserId, MemberRole newRole, Long requesterId) {
        Trip trip = findByIdOrThrow(tripId);
        checkUserIsOrganizer(trip, requesterId);

        TripMember member = tripMemberRepository.findByUser_IdAndTrip_Id(memberUserId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        // If demoting an ORGANIZER to PARTICIPANT, verify it's not the last one
        if (member.getRole() == MemberRole.ORGANIZER && newRole == MemberRole.PARTICIPANT) {
            long organizerCount = tripMemberRepository.findByTrip_IdAndRole(tripId, MemberRole.ORGANIZER).size();
            if (organizerCount <= 1) {
                throw new IllegalArgumentException("Cannot demote the only organizer");
            }
        }

        member.setRole(newRole);
        return tripMemberRepository.save(member);
    }

    /**
     * LIST TRIP MEMBERS
     */
    public List<TripMember> getTripMembers(Long tripId) {
        return tripMemberRepository.findByTrip_Id(tripId);
    }

    /**
     * LIST TRIP ORGANIZERS
     */
    public List<TripMember> getTripOrganizers(Long tripId) {
        return tripMemberRepository.findByTrip_IdAndRole(tripId, MemberRole.ORGANIZER);
    }

    // ===== AUTHORIZATION CHECKS =====

    /**
     * CHECK IF USER IS ORGANIZER
     *
     * Throws exception if not.
     */
    private void checkUserIsOrganizer(Trip trip, Long userId) {
        TripMember member = tripMemberRepository.findByUser_IdAndTrip_Id(userId, trip.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        if (!member.isOrganizer()) {
            throw new UnauthorizedException("Only organizers can perform this action");
        }
    }

    /**
     * CHECK IF USER IS MEMBER
     *
     * Throws exception if not.
     */
    public void checkUserIsMember(Trip trip, Long userId) {
        if (!tripMemberRepository.existsByUser_IdAndTrip_Id(userId, trip.getId())) {
            throw new UnauthorizedException("You are not a member of this trip");
        }
    }

    // ===== VALIDATIONS =====

    /**
     * VALIDATE TRIP DATES
     *
     * - endDate must be >= startDate
     * - Dates cannot be too far in the past (optional)
     */
    private void validateTripDates(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be equal to or after start date");
        }
    }
}
