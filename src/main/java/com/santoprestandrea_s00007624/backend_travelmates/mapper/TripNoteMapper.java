package com.santoprestandrea_s00007624.backend_travelmates.mapper;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.CreateNoteRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UpdateNoteRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.TripNoteResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.TripNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MAPPER: TRIP NOTE ↔ DTO
 *
 * Converts between TripNote entities and DTOs.
 */
@Component
public class TripNoteMapper {

    @Autowired
    private UserMapper userMapper;

    /**
     * Converts TripNote entity to TripNoteResponse DTO
     */
    public TripNoteResponse toResponse(TripNote note) {
        if (note == null) {
            return null;
        }

        return TripNoteResponse.builder()
                .id(note.getId())
                .tripId(note.getTrip().getId())
                .author(userMapper.toResponse(note.getAuthor()))
                .content(note.getContent())
                .isPinned(note.getIsPinned())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }

    /**
     * Converts CreateNoteRequest to TripNote entity
     */
    public TripNote toEntity(CreateNoteRequest request) {
        if (request == null) {
            return null;
        }

        return TripNote.builder()
                .content(request.getContent())
                .isPinned(request.getIsPinned() != null ? request.getIsPinned() : false)
                .build();
    }

    /**
     * Updates TripNote entity from UpdateNoteRequest
     * Only updates non-null fields
     */
    public void updateEntity(TripNote note, UpdateNoteRequest request) {
        if (request == null) {
            return;
        }

        if (request.getContent() != null) {
            note.setContent(request.getContent());
        }
        if (request.getIsPinned() != null) {
            note.setIsPinned(request.getIsPinned());
        }
    }
}
