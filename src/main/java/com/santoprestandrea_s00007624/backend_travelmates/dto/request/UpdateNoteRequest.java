package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO - UPDATE NOTE REQUEST
 *
 * Used to update an existing note.
 * All fields are optional.
 *
 * JSON EXAMPLE:
 * {
 * "content": "Updated content",
 * "isPinned": false
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateNoteRequest {

    @Size(max = 2000, message = "Content too long (max 2000 characters)")
    private String content;

    private Boolean isPinned;
}
