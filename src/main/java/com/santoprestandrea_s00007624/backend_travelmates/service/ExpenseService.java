package com.santoprestandrea_s00007624.backend_travelmates.service;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreatePersonalExpenseRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateSharedExpenseRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateExpenseRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.*;
import com.santoprestandrea_s00007624.backend_travelmates.entity.*;
import com.santoprestandrea_s00007624.backend_travelmates.exception.ResourceNotFoundException;
import com.santoprestandrea_s00007624.backend_travelmates.exception.UnauthorizedException;
import com.santoprestandrea_s00007624.backend_travelmates.mapper.ExpenseMapper;
import com.santoprestandrea_s00007624.backend_travelmates.mapper.UserMapper;
import com.santoprestandrea_s00007624.backend_travelmates.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SERVICE: EXPENSE MANAGEMENT
 *
 * Handles all expense operations including:
 * - Creating shared and personal expenses
 * - Calculating splits
 * - Computing trip balances
 * - Generating settlement suggestions
 */
@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SharedExpenseRepository sharedExpenseRepository;

    @Autowired
    private PersonalExpenseRepository personalExpenseRepository;

    @Autowired
    private ExpenseSplitRepository expenseSplitRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripMemberRepository tripMemberRepository;

    @Autowired
    private ExpenseMapper expenseMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * CREATE SHARED EXPENSE
     */
    @Transactional
    public SharedExpenseResponse createSharedExpense(Long tripId, CreateSharedExpenseRequest request,
            User currentUser) {
        // Verify trip exists
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        // Verify user is member of the trip
        verifyUserIsMember(tripId, currentUser.getId());

        // Create shared expense
        SharedExpense expense = new SharedExpense();
        expense.setTrip(trip);
        expense.setPaidBy(currentUser);
        expense.setAmount(request.getAmount());
        expense.setCurrency(request.getCurrency());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expense.setCategory(request.getCategory());
        expense.setReceiptImageUrl(request.getReceiptImageUrl());
        expense.setNotes(request.getNotes());
        expense.setCreatedBy(currentUser);
        expense.setSplitType(request.getSplitType());

        // Save expense first to get ID
        expense = sharedExpenseRepository.save(expense);

        // Create splits based on split type
        List<ExpenseSplit> splits = createSplits(expense, request);
        expense.setSplits(splits);

        // Save again with splits
        expense = sharedExpenseRepository.save(expense);

        return expenseMapper.toSharedExpenseResponse(expense);
    }

    /**
     * CREATE PERSONAL EXPENSE
     */
    @Transactional
    public PersonalExpenseResponse createPersonalExpense(Long tripId, CreatePersonalExpenseRequest request,
            User currentUser) {
        // Verify trip exists
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        // Verify user is member of the trip
        verifyUserIsMember(tripId, currentUser.getId());

        // Get the user who should reimburse
        User forUser = userRepository.findById(request.getForUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify forUser is also a member of the trip
        verifyUserIsMember(tripId, forUser.getId());

        // Create personal expense
        PersonalExpense expense = new PersonalExpense();
        expense.setTrip(trip);
        expense.setPaidBy(currentUser);
        expense.setAmount(request.getAmount());
        expense.setCurrency(request.getCurrency());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expense.setCategory(request.getCategory());
        expense.setReceiptImageUrl(request.getReceiptImageUrl());
        expense.setNotes(request.getNotes());
        expense.setCreatedBy(currentUser);
        expense.setForUser(forUser);
        expense.setIsPaid(false);

        expense = personalExpenseRepository.save(expense);

        return expenseMapper.toPersonalExpenseResponse(expense);
    }

    /**
     * GET ALL EXPENSES FOR A TRIP
     */
    public List<ExpenseResponse> getTripExpenses(Long tripId, User currentUser) {
        verifyUserIsMember(tripId, currentUser.getId());

        List<Expense> expenses = expenseRepository.findByTrip_IdOrderByDateDesc(tripId);

        return expenses.stream()
                .map(expenseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * GET EXPENSE BY ID
     */
    public ExpenseResponse getExpenseById(Long tripId, Long expenseId, User currentUser) {
        verifyUserIsMember(tripId, currentUser.getId());

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (!expense.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Expense does not belong to this trip");
        }

        return expenseMapper.toResponse(expense);
    }

    /**
     * UPDATE EXPENSE
     */
    @Transactional
    public ExpenseResponse updateExpense(Long tripId, Long expenseId, UpdateExpenseRequest request, User currentUser) {
        verifyUserIsMember(tripId, currentUser.getId());

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (!expense.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Expense does not belong to this trip");
        }

        // Only creator or organizer can update
        boolean isOrganizer = tripMemberRepository.findByUser_IdAndTrip_Id(currentUser.getId(), tripId)
                .map(TripMember::isOrganizer)
                .orElse(false);
        boolean isCreator = expense.getCreatedBy().getId().equals(currentUser.getId());

        if (!isOrganizer && !isCreator) {
            throw new UnauthorizedException("Only expense creator or trip organizer can update expenses");
        }

        // Update fields if provided
        if (request.getDescription() != null)
            expense.setDescription(request.getDescription());
        if (request.getAmount() != null)
            expense.setAmount(request.getAmount());
        if (request.getCurrency() != null)
            expense.setCurrency(request.getCurrency());
        if (request.getCategory() != null)
            expense.setCategory(request.getCategory());
        if (request.getDate() != null)
            expense.setDate(request.getDate());
        if (request.getReceiptImageUrl() != null)
            expense.setReceiptImageUrl(request.getReceiptImageUrl());
        if (request.getNotes() != null)
            expense.setNotes(request.getNotes());

        expense = expenseRepository.save(expense);

        return expenseMapper.toResponse(expense);
    }

    /**
     * DELETE EXPENSE
     */
    @Transactional
    public void deleteExpense(Long tripId, Long expenseId, User currentUser) {
        verifyUserIsMember(tripId, currentUser.getId());

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (!expense.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Expense does not belong to this trip");
        }

        // Only creator or organizer can delete
        boolean isOrganizer = tripMemberRepository.findByUser_IdAndTrip_Id(currentUser.getId(), tripId)
                .map(TripMember::isOrganizer)
                .orElse(false);
        boolean isCreator = expense.getCreatedBy().getId().equals(currentUser.getId());

        if (!isOrganizer && !isCreator) {
            throw new UnauthorizedException("Only expense creator or trip organizer can delete expenses");
        }

        expenseRepository.delete(expense);
    }

    /**
     * MARK EXPENSE SPLIT AS PAID
     */
    @Transactional
    public ExpenseSplitResponse markSplitAsPaid(Long splitId, User currentUser) {
        ExpenseSplit split = expenseSplitRepository.findById(splitId)
                .orElseThrow(() -> new ResourceNotFoundException("Split not found"));

        // Only the user who owes or the trip organizer can mark as paid
        Long tripId = split.getSharedExpense().getTrip().getId();
        boolean isOrganizer = tripMemberRepository.findByUser_IdAndTrip_Id(currentUser.getId(), tripId)
                .map(TripMember::isOrganizer)
                .orElse(false);
        boolean isDebtor = split.getUser().getId().equals(currentUser.getId());

        if (!isOrganizer && !isDebtor) {
            throw new UnauthorizedException("Only the debtor or trip organizer can mark split as paid");
        }

        split.setIsPaid(true);
        split = expenseSplitRepository.save(split);

        return expenseMapper.toExpenseSplitResponse(split);
    }

    /**
     * MARK PERSONAL EXPENSE AS PAID
     */
    @Transactional
    public PersonalExpenseResponse markPersonalExpenseAsPaid(Long tripId, Long expenseId, User currentUser) {
        verifyUserIsMember(tripId, currentUser.getId());

        PersonalExpense expense = personalExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (!expense.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Expense does not belong to this trip");
        }

        // Only the debtor or organizer can mark as paid
        boolean isOrganizer = tripMemberRepository.findByUser_IdAndTrip_Id(currentUser.getId(), tripId)
                .map(TripMember::isOrganizer)
                .orElse(false);
        boolean isDebtor = expense.getForUser().getId().equals(currentUser.getId());

        if (!isOrganizer && !isDebtor) {
            throw new UnauthorizedException("Only the debtor or trip organizer can mark as paid");
        }

        expense.setIsPaid(true);
        expense = personalExpenseRepository.save(expense);

        return expenseMapper.toPersonalExpenseResponse(expense);
    }

    /**
     * CALCULATE TRIP BALANCE (WHO OWES WHOM)
     */
    public TripBalanceResponse calculateTripBalance(Long tripId, User currentUser) {
        verifyUserIsMember(tripId, currentUser.getId());

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        // Get all trip members
        List<TripMember> members = tripMemberRepository.findByTrip_Id(tripId);

        // Calculate balances
        Map<Long, TripBalanceResponse.UserBalanceDetail> userBalances = new HashMap<>();

        for (TripMember member : members) {
            User user = member.getUser();
            BigDecimal totalPaid = calculateTotalPaid(tripId, user.getId());
            BigDecimal totalOwed = calculateTotalOwed(tripId, user.getId());
            BigDecimal netBalance = totalPaid.subtract(totalOwed);

            TripBalanceResponse.UserBalanceDetail detail = TripBalanceResponse.UserBalanceDetail.builder()
                    .userId(user.getId())
                    .userName(user.getFirstName() + " " + user.getLastName())
                    .totalPaid(totalPaid)
                    .totalOwed(totalOwed)
                    .netBalance(netBalance)
                    .build();

            userBalances.put(user.getId(), detail);
        }

        // Generate settlement suggestions
        List<TripBalanceResponse.SettlementSuggestion> settlements = generateSettlements(userBalances);

        // Calculate total expenses (sum all expenses for this trip)
        List<Expense> allExpenses = expenseRepository.findByTrip_IdOrderByDateDesc(tripId);
        BigDecimal totalExpenses = allExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return TripBalanceResponse.builder()
                .tripId(tripId)
                .tripTitle(trip.getTitle())
                .totalExpenses(totalExpenses)
                .currency(trip.getCurrency())
                .userBalances(userBalances)
                .settlements(settlements)
                .build();
    }

    // ================ PRIVATE HELPER METHODS ================

    /**
     * Create splits based on split type
     */
    private List<ExpenseSplit> createSplits(SharedExpense expense, CreateSharedExpenseRequest request) {
        List<ExpenseSplit> splits = new ArrayList<>();

        switch (request.getSplitType()) {
            case EQUAL:
                splits = createEqualSplits(expense, request.getParticipantIds());
                break;
            case PERCENTAGE:
                splits = createPercentageSplits(expense, request.getSplits());
                break;
            case CUSTOM:
                splits = createCustomSplits(expense, request.getSplits());
                break;
        }

        return splits;
    }

    /**
     * Create EQUAL splits
     */
    private List<ExpenseSplit> createEqualSplits(SharedExpense expense, List<Long> participantIds) {
        if (participantIds == null || participantIds.isEmpty()) {
            throw new IllegalArgumentException("Participant list cannot be empty for EQUAL split");
        }

        BigDecimal totalAmount = expense.getAmount();
        int participantCount = participantIds.size();
        BigDecimal amountPerPerson = totalAmount.divide(
                BigDecimal.valueOf(participantCount), 2, RoundingMode.HALF_UP);

        List<ExpenseSplit> splits = new ArrayList<>();

        for (Long userId : participantIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

            ExpenseSplit split = new ExpenseSplit();
            split.setSharedExpense(expense);
            split.setUser(user);
            split.setAmount(amountPerPerson);
            split.setPercentage(null);
            split.setIsPaid(user.getId().equals(expense.getPaidBy().getId())); // Auto-mark as paid if they paid

            splits.add(split);
        }

        return splits;
    }

    /**
     * Create PERCENTAGE splits
     */
    private List<ExpenseSplit> createPercentageSplits(SharedExpense expense,
            List<CreateSharedExpenseRequest.SplitDetailRequest> splitDetails) {

        if (splitDetails == null || splitDetails.isEmpty()) {
            throw new IllegalArgumentException("Split details required for PERCENTAGE split");
        }

        // Validate percentages sum to 100
        BigDecimal totalPercentage = splitDetails.stream()
                .map(CreateSharedExpenseRequest.SplitDetailRequest::getPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPercentage.compareTo(new BigDecimal("100.00")) != 0) {
            throw new IllegalArgumentException("Percentages must sum to 100. Current sum: " + totalPercentage);
        }

        List<ExpenseSplit> splits = new ArrayList<>();

        for (CreateSharedExpenseRequest.SplitDetailRequest detail : splitDetails) {
            User user = userRepository.findById(detail.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + detail.getUserId()));

            BigDecimal amount = expense.getAmount()
                    .multiply(detail.getPercentage())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            ExpenseSplit split = new ExpenseSplit();
            split.setSharedExpense(expense);
            split.setUser(user);
            split.setAmount(amount);
            split.setPercentage(detail.getPercentage());
            split.setIsPaid(user.getId().equals(expense.getPaidBy().getId()));

            splits.add(split);
        }

        return splits;
    }

    /**
     * Create CUSTOM splits
     */
    private List<ExpenseSplit> createCustomSplits(SharedExpense expense,
            List<CreateSharedExpenseRequest.SplitDetailRequest> splitDetails) {

        if (splitDetails == null || splitDetails.isEmpty()) {
            throw new IllegalArgumentException("Split details required for CUSTOM split");
        }

        // Validate amounts sum to total expense
        BigDecimal totalSplitAmount = splitDetails.stream()
                .map(CreateSharedExpenseRequest.SplitDetailRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalSplitAmount.compareTo(expense.getAmount()) != 0) {
            throw new IllegalArgumentException(
                    "Split amounts must sum to total expense. Expected: " + expense.getAmount() +
                            ", Got: " + totalSplitAmount);
        }

        List<ExpenseSplit> splits = new ArrayList<>();

        for (CreateSharedExpenseRequest.SplitDetailRequest detail : splitDetails) {
            User user = userRepository.findById(detail.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + detail.getUserId()));

            ExpenseSplit split = new ExpenseSplit();
            split.setSharedExpense(expense);
            split.setUser(user);
            split.setAmount(detail.getAmount());
            split.setPercentage(null);
            split.setIsPaid(user.getId().equals(expense.getPaidBy().getId()));

            splits.add(split);
        }

        return splits;
    }

    /**
     * Calculate total amount paid by a user in a trip
     */
    private BigDecimal calculateTotalPaid(Long tripId, Long userId) {
        List<Expense> paidExpenses = expenseRepository.findByTrip_IdAndPaidBy_Id(tripId, userId);
        return paidExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate total amount owed by a user in a trip
     */
    private BigDecimal calculateTotalOwed(Long tripId, Long userId) {
        BigDecimal totalFromSplits = BigDecimal.ZERO;
        BigDecimal totalFromPersonal = BigDecimal.ZERO;

        // From shared expense splits
        List<ExpenseSplit> splits = expenseSplitRepository.findUnpaidSplitsByTripAndUser(tripId, userId);
        if (!splits.isEmpty()) {
            totalFromSplits = splits.stream()
                    .map(ExpenseSplit::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // From personal expenses where user owes
        List<PersonalExpense> personalExpenses = personalExpenseRepository
                .findByTrip_IdAndForUser_IdAndIsPaidFalse(tripId, userId);
        if (!personalExpenses.isEmpty()) {
            totalFromPersonal = personalExpenses.stream()
                    .map(PersonalExpense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // Also add paid splits (for total calculation)
        List<SharedExpense> sharedExpenses = sharedExpenseRepository.findByTrip_Id(tripId);
        for (SharedExpense se : sharedExpenses) {
            for (ExpenseSplit split : se.getSplits()) {
                if (split.getUser().getId().equals(userId)) {
                    if (!splits.contains(split)) { // If not already counted as unpaid
                        totalFromSplits = totalFromSplits.add(split.getAmount());
                    }
                }
            }
        }

        return totalFromSplits.add(totalFromPersonal);
    }

    /**
     * Generate optimal settlement suggestions (minimize transactions)
     */
    private List<TripBalanceResponse.SettlementSuggestion> generateSettlements(
            Map<Long, TripBalanceResponse.UserBalanceDetail> userBalances) {

        List<TripBalanceResponse.SettlementSuggestion> settlements = new ArrayList<>();

        // Separate creditors (positive balance) and debtors (negative balance)
        List<UserBalance> creditors = new ArrayList<>();
        List<UserBalance> debtors = new ArrayList<>();

        for (TripBalanceResponse.UserBalanceDetail detail : userBalances.values()) {
            if (detail.getNetBalance().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new UserBalance(detail.getUserId(), detail.getUserName(), detail.getNetBalance()));
            } else if (detail.getNetBalance().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(new UserBalance(detail.getUserId(), detail.getUserName(),
                        detail.getNetBalance().abs()));
            }
        }

        // Sort: largest amounts first
        creditors.sort((a, b) -> b.amount.compareTo(a.amount));
        debtors.sort((a, b) -> b.amount.compareTo(a.amount));

        // Greedy algorithm: match largest debtor with largest creditor
        int i = 0, j = 0;
        while (i < debtors.size() && j < creditors.size()) {
            UserBalance debtor = debtors.get(i);
            UserBalance creditor = creditors.get(j);

            BigDecimal settlementAmount = debtor.amount.min(creditor.amount);

            // Create settlement suggestion
            User debtorUser = userRepository.findById(debtor.userId).orElse(null);
            User creditorUser = userRepository.findById(creditor.userId).orElse(null);

            if (debtorUser != null && creditorUser != null) {
                settlements.add(TripBalanceResponse.SettlementSuggestion.builder()
                        .from(userMapper.toResponse(debtorUser))
                        .to(userMapper.toResponse(creditorUser))
                        .amount(settlementAmount)
                        .build());
            }

            // Update balances
            debtor.amount = debtor.amount.subtract(settlementAmount);
            creditor.amount = creditor.amount.subtract(settlementAmount);

            // Move to next if settled
            if (debtor.amount.compareTo(BigDecimal.ZERO) == 0)
                i++;
            if (creditor.amount.compareTo(BigDecimal.ZERO) == 0)
                j++;
        }

        return settlements;
    }

    /**
     * Verify user is member of trip
     */
    private void verifyUserIsMember(Long tripId, Long userId) {
        if (!tripMemberRepository.existsByUser_IdAndTrip_Id(userId, tripId)) {
            throw new UnauthorizedException("You are not a member of this trip");
        }
    }

    /**
     * Helper class for settlement calculation
     */
    private static class UserBalance {
        Long userId;
        String userName;
        BigDecimal amount;

        UserBalance(Long userId, String userName, BigDecimal amount) {
            this.userId = userId;
            this.userName = userName;
            this.amount = amount;
        }
    }
}
