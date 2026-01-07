package com.santoprestandrea_s00007624.backend_travelmates.controller;


import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateTripRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.InviteMemberRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateTripRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.TripBalanceResponse;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.TripDetailResponse;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.TripMemberResponse;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.TripResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.*;
import com.santoprestandrea_s00007624.backend_travelmates.mapper.TripMapper;
import com.santoprestandrea_s00007624.backend_travelmates.service.ExpenseService;
import com.santoprestandrea_s00007624.backend_travelmates.service.TripService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TRIP CONTROLLER
 *
 * Exposes REST API endpoints for:
 * - Trip CRUD operations
 * - Member management
 * - Trip search
 *
 * BASE URL: /api/trips
 */
@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private TripMapper tripMapper;

    @Autowired
    private ExpenseService expenseService;

    // ===== HELPER: GET CURRENT USER ID =====

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return currentUser.getId();
    }

    // ===== CREATE =====

    /**
     * POST /api/trips - CREATE NEW TRIP
     *
     * REQUEST BODY:
     * {
     * "title": "Vacation in Paris",
     * "description": "Romantic weekend",
     * "destination": "Paris, France",
     * "startDate": "2025-06-01",
     * "endDate": "2025-06-05",
     * "budget": 1500.00,
     * "currency": "EUR"
     * }
     *
     * RESPONSE: 201 CREATED + TripDetailResponse
     */
    @PostMapping
    public ResponseEntity<TripDetailResponse> createTrip(@Valid @RequestBody CreateTripRequest request) {
        Long currentUserId = getCurrentUserId();

        // Convert DTO to Entity
        Trip trip = tripMapper.toEntity(request);

        // Create trip (Service adds creator as ORGANIZER)
        Trip createdTrip = tripService.createTrip(trip, currentUserId);

        // Convert Entity to DTO
        TripDetailResponse response = tripMapper.toDetailResponse(createdTrip);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===== READ =====

    /**
     * GET /api/trips - LIST ALL TRIPS FOR CURRENT USER
     *
     * Returns all trips the user participates in (as ORGANIZER or PARTICIPANT).
     *
     * RESPONSE: 200 OK + List<TripResponse>
     */
    @GetMapping
    public ResponseEntity<List<TripResponse>> getMyTrips() {
        Long currentUserId = getCurrentUserId();
        List<Trip> trips = tripService.findTripsByUserId(currentUserId);
        List<TripResponse> response = tripMapper.toResponseList(trips);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/trips/{id} - TRIP DETAILS
     *
     * Only trip members can view details.
     *
     * RESPONSE: 200 OK + TripDetailResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<TripDetailResponse> getTripById(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();

        Trip trip = tripService.findByIdOrThrow(id);

        // Verify user is a member
        tripService.checkUserIsMember(trip, currentUserId);

        TripDetailResponse response = tripMapper.toDetailResponse(trip);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/trips/organized - TRIPS WHERE USER IS ORGANIZER
     *
     * Useful for showing "My organized trips".
     */
    @GetMapping("/organized")
    public ResponseEntity<List<TripResponse>> getTripsWhereOrganizer() {
        Long currentUserId = getCurrentUserId();
        List<Trip> trips = tripService.findTripsWhereUserIsOrganizer(currentUserId);
        List<TripResponse> response = tripMapper.toResponseList(trips);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/trips/search?keyword=paris
     *
     * Search trips by title or destination.
     * Only among current user's trips.
     */
    @GetMapping("/search")
    public ResponseEntity<List<TripResponse>> searchTrips(@RequestParam String keyword) {
        // For security, search only among user's trips
        Long currentUserId = getCurrentUserId();
        List<Trip> allUserTrips = tripService.findTripsByUserId(currentUserId);

        // Filter locally by keyword
        List<Trip> filtered = allUserTrips.stream()
                .filter(trip -> trip.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        trip.getDestination().toLowerCase().contains(keyword.toLowerCase()))
                .toList();

        List<TripResponse> response = tripMapper.toResponseList(filtered);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/trips/status/{status} - FILTER BY STATUS
     *
     * Example: /api/trips/status/ACTIVE
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TripResponse>> getTripsByStatus(@PathVariable TripStatus status) {
        Long currentUserId = getCurrentUserId();

        // Find all user's trips
        List<Trip> allUserTrips = tripService.findTripsByUserId(currentUserId);

        // Filter by status
        List<Trip> filtered = allUserTrips.stream()
                .filter(trip -> trip.getStatus() == status)
                .toList();

        List<TripResponse> response = tripMapper.toResponseList(filtered);
        return ResponseEntity.ok(response);
    }

    // ===== UPDATE =====

    /**
     * PUT /api/trips/{id} - UPDATE TRIP
     *
     * Only ORGANIZER can modify.
     * All fields are optional (partial update).
     *
     * REQUEST BODY:
     * {
     * "title": "New title",
     * "budget": 2000.00
     * }
     */
    @PutMapping("/{id}")
    public ResponseEntity<TripDetailResponse> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTripRequest request) {

        Long currentUserId = getCurrentUserId();

        Trip updatedTrip = tripService.updateTrip(id, request, currentUserId);
        TripDetailResponse response = tripMapper.toDetailResponse(updatedTrip);

        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/trips/{id}/status - CHANGE STATUS ONLY
     *
     * REQUEST BODY:
     * {
     * "status": "ACTIVE"
     * }
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TripDetailResponse> updateTripStatus(
            @PathVariable Long id,
            @RequestBody TripStatus newStatus) {

        Long currentUserId = getCurrentUserId();

        Trip updatedTrip = tripService.updateTripStatus(id, newStatus, currentUserId);
        TripDetailResponse response = tripMapper.toDetailResponse(updatedTrip);

        return ResponseEntity.ok(response);
    }

    // ===== DELETE =====

    /**
     * DELETE /api/trips/{id} - DELETE TRIP
     *
     * Only ORGANIZER can delete.
     * Also deletes all members and associated relationships.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        tripService.deleteTrip(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // ===== MEMBER MANAGEMENT =====

    /**
     * GET /api/trips/{id}/members - LIST MEMBERS
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<TripMemberResponse>> getTripMembers(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();

        Trip trip = tripService.findByIdOrThrow(id);
        tripService.checkUserIsMember(trip, currentUserId);

        List<TripMember> members = tripService.getTripMembers(id);
        List<TripMemberResponse> response = members.stream()
                .map(tripMapper::toMemberResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/trips/{id}/members - INVITE MEMBER
     *
     * Only ORGANIZER can invite.
     *
     * REQUEST BODY:
     * {
     * "userEmail": "friend@example.com",
     * "role": "PARTICIPANT"
     * }
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<TripMemberResponse> inviteMember(
            @PathVariable Long id,
            @Valid @RequestBody InviteMemberRequest request) {

        Long currentUserId = getCurrentUserId();

        TripMember newMember = tripService.inviteMember(id, request, currentUserId);
        TripMemberResponse response = tripMapper.toMemberResponse(newMember);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/trips/{id}/accept - ACCEPT INVITATION
     *
     * The invited user accepts to participate.
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<TripMemberResponse> acceptInvitation(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();

        TripMember member = tripService.acceptInvitation(id, currentUserId);
        TripMemberResponse response = tripMapper.toMemberResponse(member);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/trips/{id}/decline - DECLINE INVITATION
     */
    @PostMapping("/{id}/decline")
    public ResponseEntity<Void> declineInvitation(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        tripService.declineInvitation(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/trips/{tripId}/members/{userId} - REMOVE MEMBER
     *
     * Only ORGANIZER can remove members.
     */
    @DeleteMapping("/{tripId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long tripId,
            @PathVariable Long userId) {

        Long currentUserId = getCurrentUserId();
        tripService.removeMember(tripId, userId, currentUserId);

        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/trips/{tripId}/members/{userId}/role - CHANGE MEMBER ROLE
     *
     * Only ORGANIZER can change roles.
     *
     * REQUEST BODY:
     * {
     * "role": "ORGANIZER"
     * }
     */
    @PatchMapping("/{tripId}/members/{userId}/role")
    public ResponseEntity<TripMemberResponse> updateMemberRole(
            @PathVariable Long tripId,
            @PathVariable Long userId,
            @RequestBody MemberRole newRole) {

        Long currentUserId = getCurrentUserId();

        TripMember updatedMember = tripService.updateMemberRole(tripId, userId, newRole, currentUserId);
        TripMemberResponse response = tripMapper.toMemberResponse(updatedMember);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/trips/{tripId}/balance - CALCULATE TRIP BALANCE
     *
     * Returns who owes whom and settlement suggestions.
     */
    @GetMapping("/{tripId}/balance")
    public ResponseEntity<TripBalanceResponse> calculateTripBalance(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User currentUser) {

        TripBalanceResponse balance = expenseService.calculateTripBalance(tripId, currentUser);
        return ResponseEntity.ok(balance);
    }
}
