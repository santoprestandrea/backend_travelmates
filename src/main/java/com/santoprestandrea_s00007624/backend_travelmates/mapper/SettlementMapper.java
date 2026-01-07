package com.santoprestandrea_s00007624.backend_travelmates.mapper;

import com.santoprestandrea_s00007624.backend_travelmates.dto.response.SettlementResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.Settlement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MAPPER: SETTLEMENT ↔ DTO
 *
 * Converts between Settlement entities and DTOs.
 */
@Component
public class SettlementMapper {

    @Autowired
    private UserMapper userMapper;

    /**
     * Converts Settlement entity to SettlementResponse DTO
     */
    public SettlementResponse toResponse(Settlement settlement) {
        if (settlement == null) {
            return null;
        }

        return SettlementResponse.builder()
                .id(settlement.getId())
                .tripId(settlement.getTrip().getId())
                .fromUser(userMapper.toResponse(settlement.getFromUser()))
                .toUser(userMapper.toResponse(settlement.getToUser()))
                .amount(settlement.getAmount())
                .currency(settlement.getCurrency())
                .status(settlement.getStatus())
                .createdAt(settlement.getCreatedAt())
                .settledAt(settlement.getSettledAt())
                .notes(settlement.getNotes())
                .build();
    }
}
