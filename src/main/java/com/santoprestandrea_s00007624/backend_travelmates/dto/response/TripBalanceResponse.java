package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO - TRIP BALANCE RESPONSE
 *
 * Shows who owes whom in a trip.
 *
 * JSON EXAMPLE:
 * {
 * "tripId": 10,
 * "tripTitle": "Paris 2025",
 * "totalExpenses": 500.00,
 * "currency": "EUR",
 * "userBalances": {
 * "1": { // Mario
 * "userId": 1,
 * "userName": "Mario Rossi",
 * "totalPaid": 250.00,
 * "totalOwed": 125.00,
 * "netBalance": 125.00 // positive = others owe Mario
 * },
 * "2": { // Luca
 * "userId": 2,
 * "userName": "Luca Bianchi",
 * "totalPaid": 100.00,
 * "totalOwed": 125.00,
 * "netBalance": -25.00 // negative = Luca owes others
 * }
 * },
 * "settlements": [
 * {
 * "from": { "id": 2, "firstName": "Luca", ... },
 * "to": { "id": 1, "firstName": "Mario", ... },
 * "amount": 25.00
 * }
 * ]
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripBalanceResponse {

    private Long tripId;
    private String tripTitle;
    private BigDecimal totalExpenses;
    private String currency;
    private Map<Long, UserBalanceDetail> userBalances;
    private java.util.List<SettlementSuggestion> settlements;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserBalanceDetail {
        private Long userId;
        private String userName;
        private BigDecimal totalPaid; // How much this user paid
        private BigDecimal totalOwed; // How much this user should pay
        private BigDecimal netBalance; // totalPaid - totalOwed (+ means owed, - means owes)
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SettlementSuggestion {
        private UserResponse from;
        private UserResponse to;
        private BigDecimal amount;
    }
}
