package com.santoprestandrea_s00007624.backend_travelmates.dto.response;


import lombok.*;

import java.math.BigDecimal;

/**
 * DTO - TRIP STATISTICS
 *
 * For now we keep the basic fields.
 * We will populate them in PHASE 2 (when we have expenses).
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
     * TOTAL EXPENSES (to be implemented in PHASE 2)
     */
    private BigDecimal totalExpenses;

    /**
     * NUMBER OF ACTIVITIES (to be implemented in PHASE 3)
     */
    private Integer numberOfActivities;

    /**
     * NUMBER OF DOCUMENTS (to be implemented in PHASE 4)
     */
    private Integer numberOfDocuments;

    // For now we always return null or 0
}
