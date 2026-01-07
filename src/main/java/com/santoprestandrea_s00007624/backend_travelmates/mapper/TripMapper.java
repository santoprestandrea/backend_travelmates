package com.santoprestandrea_s00007624.backend_travelmates.mapper;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateTripRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateTripRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.*;
import com.santoprestandrea_s00007624.backend_travelmates.entity.Trip;
import com.santoprestandrea_s00007624.backend_travelmates.entity.TripMember;
import com.santoprestandrea_s00007624.backend_travelmates.repository.ActivityRepository;
import com.santoprestandrea_s00007624.backend_travelmates.repository.DocumentRepository;
import com.santoprestandrea_s00007624.backend_travelmates.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MAPPER FOR TRIP
 *
 * Converts between Entity and DTO.
 * This allows us to:
 * - NOT expose entities directly to the frontend
 * - Hide sensitive fields
 * - Add calculated fields (e.g: durationInDays, memberCount)
 */
@Component
public class TripMapper {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private DocumentRepository documentRepository;

    // ===== ENTITY → DTO =====

    /**
     * TRIP → TripResponse (basic version)
     *
     * Used for trip lists.
     * Does NOT include the complete member list (only the count).
     */
    public TripResponse toResponse(Trip trip) {
        if (trip == null) {
            return null;
        }

        return TripResponse.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .description(trip.getDescription())
                .destination(trip.getDestination())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .budget(trip.getBudget())
                .currency(trip.getCurrency())
                .coverImageUrl(trip.getCoverImageUrl())
                .status(trip.getStatus())
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .durationInDays(trip.getDurationInDays())
                .memberCount(trip.getMembers() != null ? trip.getMembers().size() : 0)
                .build();
    }

    /**
     * TRIP → TripDetailResponse (complete version)
     *
     * Used for trip detail page.
     * Includes the complete member list.
     */
    public TripDetailResponse toDetailResponse(Trip trip) {
        if (trip == null) {
            return null;
        }

        // Convert members
        List<TripMemberResponse> membersResponse = trip.getMembers() != null
                ? trip.getMembers().stream()
                        .map(this::toMemberResponse)
                        .collect(Collectors.toList())
                : List.of();

        return TripDetailResponse.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .description(trip.getDescription())
                .destination(trip.getDestination())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .budget(trip.getBudget())
                .currency(trip.getCurrency())
                .coverImageUrl(trip.getCoverImageUrl())
                .status(trip.getStatus())
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .durationInDays(trip.getDurationInDays())
                .members(membersResponse)
                .statistics(createStatistics(trip))
                .build();
    }

    /**
     * TripMember → TripMemberResponse
     */
    public TripMemberResponse toMemberResponse(TripMember member) {
        if (member == null) {
            return null;
        }

        return TripMemberResponse.builder()
                .id(member.getId())
                .user(userMapper.toResponse(member.getUser()))
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .invitationStatus(member.getInvitationStatus())
                .build();
    }

    // ===== DTO → ENTITY =====

    /**
     * CreateTripRequest → Trip
     *
     * Converts the creation request to entity.
     * NOTE: Does NOT set members, those are managed in the Service!
     */
    public Trip toEntity(CreateTripRequest request) {
        if (request == null) {
            return null;
        }

        return Trip.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .destination(request.getDestination())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .budget(request.getBudget())
                .currency(request.getCurrency() != null ? request.getCurrency() : "EUR")
                .coverImageUrl(request.getCoverImageUrl())
                .build();
        // status is automatically set to PLANNING by @Builder.Default
        // createdAt and updatedAt are set by @CreationTimestamp and @UpdateTimestamp
    }

    /**
     * UPDATE Trip from UpdateTripRequest
     *
     * Updates only the provided fields (not null).
     * PATTERN: Partial Update
     */
    public void updateEntityFromRequest(UpdateTripRequest request, Trip trip) {
        if (request == null || trip == null) {
            return;
        }

        if (request.getTitle() != null) {
            trip.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            trip.setDescription(request.getDescription());
        }
        if (request.getDestination() != null) {
            trip.setDestination(request.getDestination());
        }
        if (request.getStartDate() != null) {
            trip.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            trip.setEndDate(request.getEndDate());
        }
        if (request.getBudget() != null) {
            trip.setBudget(request.getBudget());
        }
        if (request.getCurrency() != null) {
            trip.setCurrency(request.getCurrency());
        }
        if (request.getCoverImageUrl() != null) {
            trip.setCoverImageUrl(request.getCoverImageUrl());
        }
        if (request.getStatus() != null) {
            trip.setStatus(request.getStatus());
        }
    }

    // ===== STATISTICS CALCULATION =====

    /**
     * Calcola le statistiche per un viaggio
     */
    private TripStatisticsResponse createStatistics(Trip trip) {
        Long tripId = trip.getId();

        // Calcola costo totale spese
        BigDecimal totalCost = expenseRepository.getTotalExpenses(tripId);
        if (totalCost == null) {
            totalCost = BigDecimal.ZERO;
        }

        // Conta attività
        Long totalActivities = activityRepository.countByTrip_Id(tripId);

        // Conta documenti
        Long totalDocuments = documentRepository.countByTrip_Id(tripId);

        return TripStatisticsResponse.builder()
                .totalExpenses(totalCost)
                .numberOfActivities(totalActivities != null ? totalActivities.intValue() : 0)
                .numberOfDocuments(totalDocuments != null ? totalDocuments.intValue() : 0)
                .build();
    }

    // ===== LIST CONVERSIONS =====

    /**
     * List of Trip → List of TripResponse
     */
    public List<TripResponse> toResponseList(List<Trip> trips) {
        if (trips == null) {
            return List.of();
        }
        return trips.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
