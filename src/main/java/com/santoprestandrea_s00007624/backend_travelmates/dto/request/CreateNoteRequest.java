package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO - CREATE NOTE REQUEST
 *
 * Used to create a new note in a trip.
 *
 * JSON EXAMPLE:
 * {
 * "content": "Don't forget to bring your passport!",
 * "isPinned": true
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNoteRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 2000, message = "Content too long (max 2000 characters)")
    private String content;

    private Boolean isPinned;
}
