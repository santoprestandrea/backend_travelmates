package com.santoprestandrea_s00007624.backend_travelmates.service;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateNoteRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateNoteRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.TripNoteResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.*;
import com.santoprestandrea_s00007624.backend_travelmates.exception.ResourceNotFoundException;
import com.santoprestandrea_s00007624.backend_travelmates.exception.UnauthorizedException;
import com.santoprestandrea_s00007624.backend_travelmates.mapper.TripNoteMapper;
import com.santoprestandrea_s00007624.backend_travelmates.repository.TripMemberRepository;
import com.santoprestandrea_s00007624.backend_travelmates.repository.TripNoteRepository;
import com.santoprestandrea_s00007624.backend_travelmates.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICE: TRIP NOTE MANAGEMENT
 *
 * Handles business logic for trip notes.
 */
@Service
@Transactional
public class TripNoteService {

    @Autowired
    private TripNoteRepository tripNoteRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripMemberRepository tripMemberRepository;

    @Autowired
    private TripNoteMapper tripNoteMapper;

    // ============================================
    // CREATE NOTE
    // ============================================

    /**
     * Create a new note in a trip
     * All trip members can create notes
     */
    public TripNoteResponse createNote(Long tripId, CreateNoteRequest request, User currentUser) {
        // 1. Check if trip exists
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // 2. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 3. Create note
        TripNote note = tripNoteMapper.toEntity(request);
        note.setTrip(trip);
        note.setAuthor(currentUser);

        // 4. Save and return
        TripNote savedNote = tripNoteRepository.save(note);
        return tripNoteMapper.toResponse(savedNote);
    }

    // ============================================
    // UPDATE NOTE
    // ============================================

    /**
     * Update an existing note
     * Only the author or ORGANIZER can update
     */
    public TripNoteResponse updateNote(Long tripId, Long noteId, UpdateNoteRequest request, User currentUser) {
        // 1. Check if note exists and belongs to the trip
        TripNote note = tripNoteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + noteId));

        if (!note.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Note does not belong to this trip");
        }

        // 2. Check if user is the author or ORGANIZER
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        boolean isAuthor = note.getAuthor().getId().equals(currentUser.getId());
        boolean isOrganizer = membership.getRole() == MemberRole.ORGANIZER;

        if (!isAuthor && !isOrganizer) {
            throw new UnauthorizedException("Only the author or organizers can update this note");
        }

        // 3. Update note
        tripNoteMapper.updateEntity(note, request);

        // 4. Save and return
        TripNote updatedNote = tripNoteRepository.save(note);
        return tripNoteMapper.toResponse(updatedNote);
    }

    // ============================================
    // DELETE NOTE
    // ============================================

    /**
     * Delete a note
     * Only the author or ORGANIZER can delete
     */
    public void deleteNote(Long tripId, Long noteId, User currentUser) {
        // 1. Check if note exists and belongs to the trip
        TripNote note = tripNoteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + noteId));

        if (!note.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Note does not belong to this trip");
        }

        // 2. Check if user is the author or ORGANIZER
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        boolean isAuthor = note.getAuthor().getId().equals(currentUser.getId());
        boolean isOrganizer = membership.getRole() == MemberRole.ORGANIZER;

        if (!isAuthor && !isOrganizer) {
            throw new UnauthorizedException("Only the author or organizers can delete this note");
        }

        // 3. Delete note
        tripNoteRepository.delete(note);
    }

    // ============================================
    // GET NOTES
    // ============================================

    /**
     * Get all notes for a trip with pagination
     * Pinned notes appear first
     */
    public Page<TripNoteResponse> getNotesByTrip(Long tripId, User currentUser, Pageable pageable) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get notes with pagination
        Page<TripNote> notes = tripNoteRepository.findByTrip_IdOrderByIsPinnedDescCreatedAtDesc(tripId, pageable);

        // 3. Convert to response
        return notes.map(tripNoteMapper::toResponse);
    }

    /**
     * Get all notes for a trip without pagination
     */
    public List<TripNoteResponse> getAllNotesByTrip(Long tripId, User currentUser) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get all notes
        List<TripNote> notes = tripNoteRepository.findByTrip_IdOrderByIsPinnedDescCreatedAtDesc(tripId);

        // 3. Convert to response
        return notes.stream()
                .map(tripNoteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get pinned notes only
     */
    public List<TripNoteResponse> getPinnedNotes(Long tripId, User currentUser) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get pinned notes
        List<TripNote> notes = tripNoteRepository.findByTrip_IdAndIsPinnedTrueOrderByCreatedAtDesc(tripId);

        // 3. Convert to response
        return notes.stream()
                .map(tripNoteMapper::toResponse)
                .collect(Collectors.toList());
    }
}
