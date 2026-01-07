package com.santoprestandrea_s00007624.backend_travelmates.controller;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateSettlementRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.SettlementResponse;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.TripBalanceResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.SettlementStatus;
import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import com.santoprestandrea_s00007624.backend_travelmates.service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLLER: SETTLEMENT MANAGEMENT
 *
 * REST API for managing settlements (payments between users).
 *
 * BASE URL: /api/trips/{tripId}/settlements
 */
@RestController
@RequestMapping("/api/trips/{tripId}/settlements")
public class SettlementController {

    @Autowired
    private SettlementService settlementService;

    // ============================================
    // CREATE SETTLEMENT
    // ============================================

    /**
     * POST /api/trips/{tripId}/settlements
     * Create a new settlement (payment record)
     */
    @PostMapping
    public ResponseEntity<SettlementResponse> createSettlement(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateSettlementRequest request,
            @AuthenticationPrincipal User currentUser) {
        SettlementResponse settlement = settlementService.createSettlement(tripId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(settlement);
    }

    // ============================================
    // MARK AS COMPLETED
    // ============================================

    /**
     * PUT /api/trips/{tripId}/settlements/{settlementId}/complete
     * Mark a settlement as completed
     * Only receiver or ORGANIZER can mark
     */
    @PutMapping("/{settlementId}/complete")
    public ResponseEntity<SettlementResponse> markAsCompleted(
            @PathVariable Long tripId,
            @PathVariable Long settlementId,
            @AuthenticationPrincipal User currentUser) {
        SettlementResponse settlement = settlementService.markAsCompleted(tripId, settlementId, currentUser);
        return ResponseEntity.ok(settlement);
    }

    // ============================================
    // GET SETTLEMENTS
    // ============================================

    /**
     * GET /api/trips/{tripId}/settlements
     * Get all settlements for a trip
     * Query params:
     * - status: Filter by status (optional)
     */
    @GetMapping
    public ResponseEntity<List<SettlementResponse>> getSettlements(
            @PathVariable Long tripId,
            @RequestParam(required = false) SettlementStatus status,
            @AuthenticationPrincipal User currentUser) {
        List<SettlementResponse> settlements = settlementService.getSettlementsByTrip(tripId, currentUser, status);
        return ResponseEntity.ok(settlements);
    }

    /**
     * GET /api/trips/{tripId}/settlements/my
     * Get settlements involving current user
     */
    @GetMapping("/my")
    public ResponseEntity<List<SettlementResponse>> getMySettlements(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User currentUser) {
        List<SettlementResponse> settlements = settlementService.getMySettlements(tripId, currentUser);
        return ResponseEntity.ok(settlements);
    }

    /**
     * GET /api/trips/{tripId}/settlements/balance-optimized
     * Get optimized balance considering settlements
     */
    @GetMapping("/balance-optimized")
    public ResponseEntity<TripBalanceResponse> getOptimizedBalance(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User currentUser) {
        TripBalanceResponse balance = settlementService.getOptimizedBalance(tripId, currentUser);
        return ResponseEntity.ok(balance);
    }

    // ============================================
    // DELETE SETTLEMENT
    // ============================================

    /**
     * DELETE /api/trips/{tripId}/settlements/{settlementId}
     * Delete a settlement
     * Only creator or ORGANIZER can delete
     */
    @DeleteMapping("/{settlementId}")
    public ResponseEntity<Map<String, String>> deleteSettlement(
            @PathVariable Long tripId,
            @PathVariable Long settlementId,
            @AuthenticationPrincipal User currentUser) {
        settlementService.deleteSettlement(tripId, settlementId, currentUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Settlement deleted successfully");
        return ResponseEntity.ok(response);
    }
}
