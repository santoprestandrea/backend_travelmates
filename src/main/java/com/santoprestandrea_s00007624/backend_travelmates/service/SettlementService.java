package com.santoprestandrea_s00007624.backend_travelmates.service;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateSettlementRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.SettlementResponse;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.TripBalanceResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.*;
import com.santoprestandrea_s00007624.backend_travelmates.exception.ResourceNotFoundException;
import com.santoprestandrea_s00007624.backend_travelmates.exception.UnauthorizedException;
import com.santoprestandrea_s00007624.backend_travelmates.mapper.SettlementMapper;
import com.santoprestandrea_s00007624.backend_travelmates.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SERVICE: SETTLEMENT MANAGEMENT
 *
 * Handles business logic for settlements (payments between users).
 */
@Service
@Transactional
public class SettlementService {

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripMemberRepository tripMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private SettlementMapper settlementMapper;

    // ============================================
    // CREATE SETTLEMENT
    // ============================================

    /**
     * Create a new settlement (payment record)
     */
    public SettlementResponse createSettlement(Long tripId, CreateSettlementRequest request, User currentUser) {
        // 1. Check if trip exists
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // 2. Check if current user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 3. Check if toUser is a member
        User toUser = userRepository.findById(request.getToUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getToUserId()));

        TripMember toUserMembership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, toUser.getId())
                .orElseThrow(() -> new UnauthorizedException("Target user is not a member of this trip"));

        // 4. Validate: cannot create settlement to yourself
        if (currentUser.getId().equals(toUser.getId())) {
            throw new IllegalArgumentException("Cannot create settlement to yourself");
        }

        // 5. Create settlement
        Settlement settlement = Settlement.builder()
                .trip(trip)
                .fromUser(currentUser)
                .toUser(toUser)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(SettlementStatus.PENDING)
                .notes(request.getNotes())
                .build();

        // 6. Save and return
        Settlement savedSettlement = settlementRepository.save(settlement);
        return settlementMapper.toResponse(savedSettlement);
    }

    // ============================================
    // MARK AS COMPLETED
    // ============================================

    /**
     * Mark a settlement as completed
     * Only the receiver (toUser) or ORGANIZER can mark as completed
     */
    public SettlementResponse markAsCompleted(Long tripId, Long settlementId, User currentUser) {
        // 1. Check if settlement exists
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement not found with id: " + settlementId));

        if (!settlement.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Settlement does not belong to this trip");
        }

        // 2. Check if user is the receiver or ORGANIZER
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        boolean isReceiver = settlement.getToUser().getId().equals(currentUser.getId());
        boolean isOrganizer = membership.getRole() == MemberRole.ORGANIZER;

        if (!isReceiver && !isOrganizer) {
            throw new UnauthorizedException("Only the receiver or organizers can mark settlement as completed");
        }

        // 3. Mark as completed
        settlement.setStatus(SettlementStatus.COMPLETED);
        settlement.setSettledAt(LocalDateTime.now());

        Settlement updatedSettlement = settlementRepository.save(settlement);
        return settlementMapper.toResponse(updatedSettlement);
    }

    // ============================================
    // GET SETTLEMENTS
    // ============================================

    /**
     * Get all settlements for a trip
     */
    public List<SettlementResponse> getSettlementsByTrip(Long tripId, User currentUser, SettlementStatus status) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get settlements
        List<Settlement> settlements;
        if (status != null) {
            settlements = settlementRepository.findByTrip_IdAndStatusOrderByCreatedAtDesc(tripId, status);
        } else {
            settlements = settlementRepository.findByTrip_IdOrderByCreatedAtDesc(tripId);
        }

        // 3. Convert to response
        return settlements.stream()
                .map(settlementMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get settlements involving current user
     */
    public List<SettlementResponse> getMySettlements(Long tripId, User currentUser) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get user's settlements
        List<Settlement> settlements = settlementRepository.findByTripAndUser(tripId, currentUser.getId());

        // 3. Convert to response
        return settlements.stream()
                .map(settlementMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ============================================
    // CALCULATE OPTIMIZED DEBTS
    // ============================================

    /**
     * Calculate optimized balance including settlements
     * This shows the net balance after considering both expenses and settlements
     */
    public TripBalanceResponse getOptimizedBalance(Long tripId, User currentUser) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get balance from expenses
        TripBalanceResponse expenseBalance = expenseService.calculateTripBalance(tripId, currentUser);

        // 3. Adjust for completed settlements
        List<Settlement> completedSettlements = settlementRepository.findByTrip_IdAndStatusOrderByCreatedAtDesc(
                tripId, SettlementStatus.COMPLETED);

        Map<Long, TripBalanceResponse.UserBalanceDetail> adjustedBalances = new HashMap<>(
                expenseBalance.getUserBalances());

        for (Settlement settlement : completedSettlements) {
            Long fromUserId = settlement.getFromUser().getId();
            Long toUserId = settlement.getToUser().getId();
            BigDecimal amount = settlement.getAmount();

            // Adjust balances
            TripBalanceResponse.UserBalanceDetail fromBalance = adjustedBalances.get(fromUserId);
            if (fromBalance != null) {
                fromBalance.setNetBalance(fromBalance.getNetBalance().add(amount));
            }

            TripBalanceResponse.UserBalanceDetail toBalance = adjustedBalances.get(toUserId);
            if (toBalance != null) {
                toBalance.setNetBalance(toBalance.getNetBalance().subtract(amount));
            }
        }

        // 4. Create optimized balance response
        return TripBalanceResponse.builder()
                .tripId(tripId)
                .totalExpenses(expenseBalance.getTotalExpenses())
                .currency(expenseBalance.getCurrency())
                .userBalances(adjustedBalances)
                .settlements(expenseBalance.getSettlements())
                .build();
    }

    /**
     * Delete a settlement
     * Only the creator or ORGANIZER can delete
     */
    public void deleteSettlement(Long tripId, Long settlementId, User currentUser) {
        // 1. Check if settlement exists
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement not found with id: " + settlementId));

        if (!settlement.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Settlement does not belong to this trip");
        }

        // 2. Check if user is the creator or ORGANIZER
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        boolean isCreator = settlement.getFromUser().getId().equals(currentUser.getId());
        boolean isOrganizer = membership.getRole() == MemberRole.ORGANIZER;

        if (!isCreator && !isOrganizer) {
            throw new UnauthorizedException("Only the creator or organizers can delete this settlement");
        }

        // 3. Delete settlement
        settlementRepository.delete(settlement);
    }
}
