package com.santoprestandrea_s00007624.backend_travelmates.entity;

/**
 * SPLIT TYPE FOR SHARED EXPENSES
 *
 * Defines how a shared expense should be divided among participants.
 */
public enum SplitType {
    
    /**
     * EQUAL - Divide expense equally among all participants
     * Example: €100 / 4 people = €25 each
     */
    EQUAL,
    
    /**
     * PERCENTAGE - Each person pays a specific percentage
     * Example: Person A: 50%, Person B: 30%, Person C: 20%
     */
    PERCENTAGE,
    
    /**
     * CUSTOM - Each person pays a custom amount
     * Example: Person A: €40, Person B: €35, Person C: €25
     */
    CUSTOM
}
