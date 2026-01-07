package com.santoprestandrea_s00007624.backend_travelmates.controller;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreatePersonalExpenseRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateSharedExpenseRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateExpenseRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.*;
import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import com.santoprestandrea_s00007624.backend_travelmates.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLLER: EXPENSE MANAGEMENT
 *
 * Handles all expense-related endpoints.
 *
 * BASE PATH: /api/trips/{tripId}/expenses
 */
@RestController
@RequestMapping("/api/trips/{tripId}/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    /**
     * CREATE SHARED EXPENSE
     *
     * POST /api/trips/{tripId}/expenses/shared
     *
     * Example request:
     * {
     * "description": "Dinner at restaurant",
     * "amount": 120.00,
     * "currency": "EUR",
     * "category": "FOOD",
     * "date": "2025-06-15",
     * "splitType": "EQUAL",
     * "participantIds": [1, 2, 3, 4]
     * }
     */
    @PostMapping("/shared")
    public ResponseEntity<SharedExpenseResponse> createSharedExpense(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateSharedExpenseRequest request,
            @AuthenticationPrincipal User currentUser) {

        SharedExpenseResponse response = expenseService.createSharedExpense(tripId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * CREATE PERSONAL EXPENSE
     *
     * POST /api/trips/{tripId}/expenses/personal
     *
     * Example request:
     * {
     * "description": "Train ticket for Luca",
     * "amount": 50.00,
     * "currency": "EUR",
     * "category": "TRANSPORT",
     * "date": "2025-06-10",
     * "forUserId": 5
     * }
     */
    @PostMapping("/personal")
    public ResponseEntity<PersonalExpenseResponse> createPersonalExpense(
            @PathVariable Long tripId,
            @Valid @RequestBody CreatePersonalExpenseRequest request,
            @AuthenticationPrincipal User currentUser) {

        PersonalExpenseResponse response = expenseService.createPersonalExpense(tripId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET ALL EXPENSES FOR A TRIP
     *
     * GET /api/trips/{tripId}/expenses
     *
     * Returns all expenses (shared and personal) for the trip.
     */
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getTripExpenses(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User currentUser) {

        List<ExpenseResponse> expenses = expenseService.getTripExpenses(tripId, currentUser);
        return ResponseEntity.ok(expenses);
    }

    /**
     * GET EXPENSE BY ID
     *
     * GET /api/trips/{tripId}/expenses/{expenseId}
     */
    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @PathVariable Long tripId,
            @PathVariable Long expenseId,
            @AuthenticationPrincipal User currentUser) {

        ExpenseResponse expense = expenseService.getExpenseById(tripId, expenseId, currentUser);
        return ResponseEntity.ok(expense);
    }

    /**
     * UPDATE EXPENSE
     *
     * PUT /api/trips/{tripId}/expenses/{expenseId}
     *
     * Example request:
     * {
     * "description": "Updated dinner description",
     * "amount": 150.00
     * }
     */
    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long tripId,
            @PathVariable Long expenseId,
            @Valid @RequestBody UpdateExpenseRequest request,
            @AuthenticationPrincipal User currentUser) {

        ExpenseResponse response = expenseService.updateExpense(tripId, expenseId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE EXPENSE
     *
     * DELETE /api/trips/{tripId}/expenses/{expenseId}
     */
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long tripId,
            @PathVariable Long expenseId,
            @AuthenticationPrincipal User currentUser) {

        expenseService.deleteExpense(tripId, expenseId, currentUser);
        return ResponseEntity.noContent().build();
    }

    /**
     * MARK PERSONAL EXPENSE AS PAID
     *
     * PATCH /api/trips/{tripId}/expenses/personal/{expenseId}/mark-paid
     */
    @PatchMapping("/personal/{expenseId}/mark-paid")
    public ResponseEntity<PersonalExpenseResponse> markPersonalExpenseAsPaid(
            @PathVariable Long tripId,
            @PathVariable Long expenseId,
            @AuthenticationPrincipal User currentUser) {

        PersonalExpenseResponse response = expenseService.markPersonalExpenseAsPaid(tripId, expenseId, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * MARK EXPENSE SPLIT AS PAID
     *
     * PATCH /api/expenses/splits/{splitId}/mark-paid
     *
     * Note: This endpoint is at root level, not under trips
     */
    @PatchMapping("/splits/{splitId}/mark-paid")
    public ResponseEntity<ExpenseSplitResponse> markSplitAsPaid(
            @PathVariable Long splitId,
            @AuthenticationPrincipal User currentUser) {

        ExpenseSplitResponse response = expenseService.markSplitAsPaid(splitId, currentUser);
        return ResponseEntity.ok(response);
    }
}
