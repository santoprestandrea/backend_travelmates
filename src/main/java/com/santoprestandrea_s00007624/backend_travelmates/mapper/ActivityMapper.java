package com.santoprestandrea_s00007624.backend_travelmates.mapper;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateActivityRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateActivityRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.ActivityResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MAPPER: ACTIVITY ↔ DTO
 *
 * Converts between Activity entities and DTOs.
 */
@Component
public class ActivityMapper {

    @Autowired
    private UserMapper userMapper;

    /**
     * Converts Activity entity to ActivityResponse DTO
     */
    public ActivityResponse toResponse(Activity activity) {
        if (activity == null) {
            return null;
        }

        return ActivityResponse.builder()
                .id(activity.getId())
                .tripId(activity.getTrip().getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .scheduledDate(activity.getScheduledDate())
                .duration(activity.getDuration())
                .location(activity.getLocation())
                .category(activity.getCategory())
                .cost(activity.getCost())
                .currency(activity.getCurrency())
                .bookingUrl(activity.getBookingUrl())
                .bookingReference(activity.getBookingReference())
                .isConfirmed(activity.getIsConfirmed())
                .isCancelled(activity.getIsCancelled())
                .createdBy(userMapper.toResponse(activity.getCreatedBy()))
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .notes(activity.getNotes())
                .build();
    }

    /**
     * Converts CreateActivityRequest to Activity entity
     */
    public Activity toEntity(CreateActivityRequest request) {
        if (request == null) {
            return null;
        }

        return Activity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .scheduledDate(request.getScheduledDate())
                .duration(request.getDuration())
                .location(request.getLocation())
                .category(request.getCategory())
                .cost(request.getCost())
                .currency(request.getCurrency())
                .bookingUrl(request.getBookingUrl())
                .bookingReference(request.getBookingReference())
                .isConfirmed(request.getIsConfirmed() != null ? request.getIsConfirmed() : false)
                .isCancelled(false)
                .notes(request.getNotes())
                .build();
    }

    /**
     * Updates Activity entity from UpdateActivityRequest
     * Only updates non-null fields!
     */
    public void updateEntity(Activity activity, UpdateActivityRequest request) {
        if (request == null) {
            return;
        }

        if (request.getTitle() != null) {
            activity.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            activity.setDescription(request.getDescription());
        }
        if (request.getScheduledDate() != null) {
            activity.setScheduledDate(request.getScheduledDate());
        }
        if (request.getDuration() != null) {
            activity.setDuration(request.getDuration());
        }
        if (request.getLocation() != null) {
            activity.setLocation(request.getLocation());
        }
        if (request.getCategory() != null) {
            activity.setCategory(request.getCategory());
        }
        if (request.getCost() != null) {
            activity.setCost(request.getCost());
        }
        if (request.getCurrency() != null) {
            activity.setCurrency(request.getCurrency());
        }
        if (request.getBookingUrl() != null) {
            activity.setBookingUrl(request.getBookingUrl());
        }
        if (request.getBookingReference() != null) {
            activity.setBookingReference(request.getBookingReference());
        }
        if (request.getIsConfirmed() != null) {
            activity.setIsConfirmed(request.getIsConfirmed());
        }
        if (request.getIsCancelled() != null) {
            activity.setIsCancelled(request.getIsCancelled());
        }
        if (request.getNotes() != null) {
            activity.setNotes(request.getNotes());
        }
    }
}
