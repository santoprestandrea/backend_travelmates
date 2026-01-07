package com.santoprestandrea_s00007624.backend_travelmates.controller;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateNoteRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateNoteRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.TripNoteResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import com.santoprestandrea_s00007624.backend_travelmates.service.TripNoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLLER: TRIP NOTE MANAGEMENT
 *
 * REST API for managing trip notes/messages.
 *
 * BASE URL: /api/trips/{tripId}/notes
 */
@RestController
@RequestMapping("/api/trips/{tripId}/notes")
public class TripNoteController {

    @Autowired
    private TripNoteService tripNoteService;

    // ============================================
    // CREATE NOTE
    // ============================================

    /**
     * POST /api/trips/{tripId}/notes
     * Create a new note in a trip
     * All trip members can create notes
     */
    @PostMapping
    public ResponseEntity<TripNoteResponse> createNote(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateNoteRequest request,
            @AuthenticationPrincipal User currentUser) {
        TripNoteResponse note = tripNoteService.createNote(tripId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    // ============================================
    // UPDATE NOTE
    // ============================================

    /**
     * PUT /api/trips/{tripId}/notes/{noteId}
     * Update an existing note
     * Only author or ORGANIZER can update
     */
    @PutMapping("/{noteId}")
    public ResponseEntity<TripNoteResponse> updateNote(
            @PathVariable Long tripId,
            @PathVariable Long noteId,
            @Valid @RequestBody UpdateNoteRequest request,
            @AuthenticationPrincipal User currentUser) {
        TripNoteResponse note = tripNoteService.updateNote(tripId, noteId, request, currentUser);
        return ResponseEntity.ok(note);
    }

    // ============================================
    // DELETE NOTE
    // ============================================

    /**
     * DELETE /api/trips/{tripId}/notes/{noteId}
     * Delete a note
     * Only author or ORGANIZER can delete
     */
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Map<String, String>> deleteNote(
            @PathVariable Long tripId,
            @PathVariable Long noteId,
            @AuthenticationPrincipal User currentUser) {
        tripNoteService.deleteNote(tripId, noteId, currentUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Note deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ============================================
    // GET NOTES
    // ============================================

    /**
     * GET /api/trips/{tripId}/notes
     * Get notes for a trip with pagination
     * Query params:
     * - page: Page number (default: 0)
     * - size: Page size (default: 20)
     */
    @GetMapping
    public ResponseEntity<Page<TripNoteResponse>> getNotes(
            @PathVariable Long tripId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TripNoteResponse> notes = tripNoteService.getNotesByTrip(tripId, currentUser, pageable);
        return ResponseEntity.ok(notes);
    }

    /**
     * GET /api/trips/{tripId}/notes/all
     * Get all notes for a trip without pagination
     */
    @GetMapping("/all")
    public ResponseEntity<List<TripNoteResponse>> getAllNotes(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User currentUser) {
        List<TripNoteResponse> notes = tripNoteService.getAllNotesByTrip(tripId, currentUser);
        return ResponseEntity.ok(notes);
    }

    /**
     * GET /api/trips/{tripId}/notes/pinned
     * Get pinned notes only
     */
    @GetMapping("/pinned")
    public ResponseEntity<List<TripNoteResponse>> getPinnedNotes(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User currentUser) {
        List<TripNoteResponse> notes = tripNoteService.getPinnedNotes(tripId, currentUser);
        return ResponseEntity.ok(notes);
    }
}
