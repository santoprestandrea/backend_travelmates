package com.santoprestandrea_s00007624.backend_travelmates.service;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateActivityRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateActivityRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.ActivityResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.*;
import com.santoprestandrea_s00007624.backend_travelmates.exception.ResourceNotFoundException;
import com.santoprestandrea_s00007624.backend_travelmates.exception.UnauthorizedException;
import com.santoprestandrea_s00007624.backend_travelmates.mapper.ActivityMapper;
import com.santoprestandrea_s00007624.backend_travelmates.repository.ActivityRepository;
import com.santoprestandrea_s00007624.backend_travelmates.repository.TripMemberRepository;
import com.santoprestandrea_s00007624.backend_travelmates.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICE: ACTIVITY MANAGEMENT
 *
 * Handles business logic for trip activities.
 */
@Service
@Transactional
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripMemberRepository tripMemberRepository;

    @Autowired
    private ActivityMapper activityMapper;

    // ============================================
    // CREATE ACTIVITY
    // ============================================

    /**
     * Create a new activity for a trip
     * Only ORGANIZER can create activities
     */
    public ActivityResponse createActivity(Long tripId, CreateActivityRequest request, User currentUser) {
        // 1. Check if trip exists
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // 2. Check if user is a member with ORGANIZER role
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        if (membership.getRole() != MemberRole.ORGANIZER) {
            throw new UnauthorizedException("Only organizers can create activities");
        }

        // 3. Create activity
        Activity activity = activityMapper.toEntity(request);
        activity.setTrip(trip);
        activity.setCreatedBy(currentUser);

        // 4. Save and return
        Activity savedActivity = activityRepository.save(activity);
        return activityMapper.toResponse(savedActivity);
    }

    // ============================================
    // UPDATE ACTIVITY
    // ============================================

    /**
     * Update an existing activity
     * Only ORGANIZER can update activities
     */
    public ActivityResponse updateActivity(Long tripId, Long activityId, UpdateActivityRequest request,
            User currentUser) {
        // 1. Check if activity exists and belongs to the trip
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id: " + activityId));

        if (!activity.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Activity does not belong to this trip");
        }

        // 2. Check if user is ORGANIZER
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        if (membership.getRole() != MemberRole.ORGANIZER) {
            throw new UnauthorizedException("Only organizers can update activities");
        }

        // 3. Update activity
        activityMapper.updateEntity(activity, request);

        // 4. Save and return
        Activity updatedActivity = activityRepository.save(activity);
        return activityMapper.toResponse(updatedActivity);
    }

    // ============================================
    // DELETE ACTIVITY
    // ============================================

    /**
     * Delete an activity
     * Only ORGANIZER can delete activities
     */
    public void deleteActivity(Long tripId, Long activityId, User currentUser) {
        // 1. Check if activity exists and belongs to the trip
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id: " + activityId));

        if (!activity.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Activity does not belong to this trip");
        }

        // 2. Check if user is ORGANIZER
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        if (membership.getRole() != MemberRole.ORGANIZER) {
            throw new UnauthorizedException("Only organizers can delete activities");
        }

        // 3. Delete activity
        activityRepository.delete(activity);
    }

    // ============================================
    // GET ACTIVITIES
    // ============================================

    /**
     * Get all activities for a trip
     * All trip members can view activities
     */
    public List<ActivityResponse> getActivitiesByTrip(Long tripId, User currentUser, ActivityCategory category,
            Boolean upcomingOnly) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get activities based on filters
        List<Activity> activities;

        if (upcomingOnly != null && upcomingOnly) {
            // Get only upcoming activities
            activities = activityRepository.findUpcomingActivities(tripId, LocalDateTime.now());
        } else if (category != null) {
            // Get activities by category
            activities = activityRepository.findByTrip_IdAndCategoryOrderByScheduledDateAsc(tripId, category);
        } else {
            // Get all activities
            activities = activityRepository.findActiveActivitiesByTripId(tripId);
        }

        // 3. Convert to response
        return activities.stream()
                .map(activityMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a single activity by ID
     */
    public ActivityResponse getActivityById(Long tripId, Long activityId, User currentUser) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get activity
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id: " + activityId));

        // 3. Verify activity belongs to trip
        if (!activity.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Activity does not belong to this trip");
        }

        // 4. Return response
        return activityMapper.toResponse(activity);
    }

    /**
     * Get upcoming activities for a trip (next 7 days)
     */
    public List<ActivityResponse> getUpcomingActivities(Long tripId, User currentUser) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get upcoming activities
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekFromNow = now.plusDays(7);

        List<Activity> activities = activityRepository.findByTripIdAndScheduledDateBetween(
                tripId, now, weekFromNow);

        // 3. Convert to response
        return activities.stream()
                .map(activityMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get confirmed activities for a trip
     */
    public List<ActivityResponse> getConfirmedActivities(Long tripId, User currentUser) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get confirmed activities
        List<Activity> activities = activityRepository.findByTrip_IdAndIsConfirmedTrueOrderByScheduledDateAsc(tripId);

        // 3. Convert to response
        return activities.stream()
                .map(activityMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cancel an activity (soft delete)
     */
    public ActivityResponse cancelActivity(Long tripId, Long activityId, User currentUser) {
        // 1. Check if activity exists
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id: " + activityId));

        if (!activity.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Activity does not belong to this trip");
        }

        // 2. Check if user is ORGANIZER
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        if (membership.getRole() != MemberRole.ORGANIZER) {
            throw new UnauthorizedException("Only organizers can cancel activities");
        }

        // 3. Cancel activity
        activity.setIsCancelled(true);
        Activity cancelledActivity = activityRepository.save(activity);

        return activityMapper.toResponse(cancelledActivity);
    }
}
