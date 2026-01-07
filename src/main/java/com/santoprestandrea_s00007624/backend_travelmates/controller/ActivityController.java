package com.santoprestandrea_s00007624.backend_travelmates.controller;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateActivityRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateActivityRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.ActivityResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.ActivityCategory;
import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import com.santoprestandrea_s00007624.backend_travelmates.service.ActivityService;
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
 * CONTROLLER: ACTIVITY MANAGEMENT
 *
 * REST API for managing trip activities.
 *
 * BASE URL: /api/trips/{tripId}/activities
 */
@RestController
@RequestMapping("/api/trips/{tripId}/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // ============================================
    // CREATE ACTIVITY
    // ============================================

    /**
     * POST /api/trips/{tripId}/activities
     * Create a new activity for a trip
     * Only ORGANIZER can create activities
     */
    @PostMapping
    public ResponseEntity<ActivityResponse> createActivity(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateActivityRequest request,
            @AuthenticationPrincipal User currentUser) {
        ActivityResponse activity = activityService.createActivity(tripId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(activity);
    }

    // ============================================
    // UPDATE ACTIVITY
    // ============================================

    /**
     * PUT /api/trips/{tripId}/activities/{activityId}
     * Update an existing activity
     * Only ORGANIZER can update
     */
    @PutMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> updateActivity(
            @PathVariable Long tripId,
            @PathVariable Long activityId,
            @Valid @RequestBody UpdateActivityRequest request,
            @AuthenticationPrincipal User currentUser) {
        ActivityResponse activity = activityService.updateActivity(tripId, activityId, request, currentUser);
        return ResponseEntity.ok(activity);
    }

    // ============================================
    // DELETE ACTIVITY
    // ============================================

    /**
     * DELETE /api/trips/{tripId}/activities/{activityId}
     * Delete an activity
     * Only ORGANIZER can delete
     */
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Map<String, String>> deleteActivity(
            @PathVariable Long tripId,
            @PathVariable Long activityId,
            @AuthenticationPrincipal User currentUser) {
        activityService.deleteActivity(tripId, activityId, currentUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Activity deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ============================================
    // GET ACTIVITIES
    // ============================================

    /**
     * GET /api/trips/{tripId}/activities
     * Get all activities for a trip
     * Query params:
     * - category: Filter by category (optional)
     * - upcomingOnly: Show only upcoming activities (optional, default: false)
     */
    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getActivities(
            @PathVariable Long tripId,
            @RequestParam(required = false) ActivityCategory category,
            @RequestParam(required = false, defaultValue = "false") Boolean upcomingOnly,
            @AuthenticationPrincipal User currentUser) {
        List<ActivityResponse> activities = activityService.getActivitiesByTrip(
                tripId, currentUser, category, upcomingOnly);
        return ResponseEntity.ok(activities);
    }

    /**
     * GET /api/trips/{tripId}/activities/{activityId}
     * Get a single activity by ID
     */
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivityById(
            @PathVariable Long tripId,
            @PathVariable Long activityId,
            @AuthenticationPrincipal User currentUser) {
        ActivityResponse activity = activityService.getActivityById(tripId, activityId, currentUser);
        return ResponseEntity.ok(activity);
    }

    /**
     * GET /api/trips/{tripId}/activities/upcoming
     * Get upcoming activities (next 7 days)
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<ActivityResponse>> getUpcomingActivities(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User currentUser) {
        List<ActivityResponse> activities = activityService.getUpcomingActivities(tripId, currentUser);
        return ResponseEntity.ok(activities);
    }

    /**
     * GET /api/trips/{tripId}/activities/confirmed
     * Get confirmed activities only
     */
    @GetMapping("/confirmed")
    public ResponseEntity<List<ActivityResponse>> getConfirmedActivities(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User currentUser) {
        List<ActivityResponse> activities = activityService.getConfirmedActivities(tripId, currentUser);
        return ResponseEntity.ok(activities);
    }

    // ============================================
    // CANCEL ACTIVITY
    // ============================================

    /**
     * PUT /api/trips/{tripId}/activities/{activityId}/cancel
     * Cancel an activity (soft delete)
     * Only ORGANIZER can cancel
     */
    @PutMapping("/{activityId}/cancel")
    public ResponseEntity<ActivityResponse> cancelActivity(
            @PathVariable Long tripId,
            @PathVariable Long activityId,
            @AuthenticationPrincipal User currentUser) {
        ActivityResponse activity = activityService.cancelActivity(tripId, activityId, currentUser);
        return ResponseEntity.ok(activity);
    }
}
