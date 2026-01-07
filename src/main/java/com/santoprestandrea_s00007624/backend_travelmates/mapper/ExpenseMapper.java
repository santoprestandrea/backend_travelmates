package com.santoprestandrea_s00007624.backend_travelmates.mapper;

import com.santoprestandrea_s00007624.backend_travelmates.dto.response.*;
import com.santoprestandrea_s00007624.backend_travelmates.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * MAPPER: EXPENSE → DTO
 *
 * Converts Expense entities to response DTOs.
 */
@Component
public class ExpenseMapper {

    @Autowired
    private UserMapper userMapper;

    /**
     * Converts Expense to appropriate response type (polymorphic)
     */
    public ExpenseResponse toResponse(Expense expense) {
        if (expense instanceof SharedExpense) {
            return toSharedExpenseResponse((SharedExpense) expense);
        } else if (expense instanceof PersonalExpense) {
            return toPersonalExpenseResponse((PersonalExpense) expense);
        }

        // Fallback to base response
        return toBaseResponse(expense);
    }

    /**
     * Base expense response
     */
    private ExpenseResponse toBaseResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .tripId(expense.getTrip().getId())
                .paidBy(userMapper.toResponse(expense.getPaidBy()))
                .amount(expense.getAmount())
                .currency(expense.getCurrency())
                .description(expense.getDescription())
                .date(expense.getDate())
                .category(expense.getCategory())
                .receiptImageUrl(expense.getReceiptImageUrl())
                .notes(expense.getNotes())
                .createdAt(expense.getCreatedAt())
                .createdBy(userMapper.toResponse(expense.getCreatedBy()))
                .expenseType("UNKNOWN")
                .build();
    }

    /**
     * Shared expense response (includes splits)
     */
    public SharedExpenseResponse toSharedExpenseResponse(SharedExpense expense) {
        return SharedExpenseResponse.sharedExpenseBuilder()
                .id(expense.getId())
                .tripId(expense.getTrip().getId())
                .paidBy(userMapper.toResponse(expense.getPaidBy()))
                .amount(expense.getAmount())
                .currency(expense.getCurrency())
                .description(expense.getDescription())
                .date(expense.getDate())
                .category(expense.getCategory())
                .receiptImageUrl(expense.getReceiptImageUrl())
                .notes(expense.getNotes())
                .createdAt(expense.getCreatedAt())
                .createdBy(userMapper.toResponse(expense.getCreatedBy()))
                .expenseType("SHARED")
                .splitType(expense.getSplitType())
                .splits(expense.getSplits().stream()
                        .map(this::toExpenseSplitResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Personal expense response
     */
    public PersonalExpenseResponse toPersonalExpenseResponse(PersonalExpense expense) {
        return PersonalExpenseResponse.personalExpenseBuilder()
                .id(expense.getId())
                .tripId(expense.getTrip().getId())
                .paidBy(userMapper.toResponse(expense.getPaidBy()))
                .amount(expense.getAmount())
                .currency(expense.getCurrency())
                .description(expense.getDescription())
                .date(expense.getDate())
                .category(expense.getCategory())
                .receiptImageUrl(expense.getReceiptImageUrl())
                .notes(expense.getNotes())
                .createdAt(expense.getCreatedAt())
                .createdBy(userMapper.toResponse(expense.getCreatedBy()))
                .expenseType("PERSONAL")
                .forUser(userMapper.toResponse(expense.getForUser()))
                .isPaid(expense.getIsPaid())
                .build();
    }

    /**
     * Expense split response
     */
    public ExpenseSplitResponse toExpenseSplitResponse(ExpenseSplit split) {
        return ExpenseSplitResponse.builder()
                .id(split.getId())
                .user(userMapper.toResponse(split.getUser()))
                .amount(split.getAmount())
                .percentage(split.getPercentage())
                .isPaid(split.getIsPaid())
                .build();
    }
}
