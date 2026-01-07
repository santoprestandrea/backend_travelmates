package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO - TRIP STATISTICS
 *
 * Statistics about a trip including expenses, activities, and documents.
 *
 * JSON EXAMPLE:
 * {
 * "totalExpenses": 850.50,
 * "numberOfActivities": 5,
 * "numberOfDocuments": 3
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripStatisticsResponse {

    /**
     * TOTAL EXPENSES
     */
    private BigDecimal totalExpenses;

    /**
     * NUMBER OF ACTIVITIES
     */
    private Integer numberOfActivities;

    /**
     * NUMBER OF DOCUMENTS
     */
    private Integer numberOfDocuments;
}
