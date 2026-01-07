package com.santoprestandrea_s00007624.backend_travelmates.entity;

/**
 * SETTLEMENT STATUS
 *
 * Represents the status of a payment between users.
 */
public enum SettlementStatus {

    /**
     * PENDING - Payment not yet completed
     */
    PENDING,

    /**
     * COMPLETED - Payment completed successfully
     */
    COMPLETED,

    /**
     * CANCELLED - Payment cancelled
     */
    CANCELLED
}
