package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import com.santoprestandrea_s00007624.backend_travelmates.entity.DocumentCategory;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO - DOCUMENT RESPONSE
 *
 * Represents a document in API responses.
 *
 * JSON EXAMPLE:
 * {
 * "id": 1,
 * "tripId": 10,
 * "uploadedBy": {
 * "id": 1,
 * "firstName": "Mario",
 * "lastName": "Rossi"
 * },
 * "fileName": "flight-ticket-rome.pdf",
 * "fileUrl": "https://cloudinary.com/files/abc123.pdf",
 * "fileType": "application/pdf",
 * "fileSize": 2048576,
 * "category": "TICKET",
 * "description": "Flight ticket from Paris to Rome",
 * "uploadDate": "2025-01-07T14:30:00",
 * "notes": "Remember to print this"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {

    private Long id;
    private Long tripId;
    private UserResponse uploadedBy;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private DocumentCategory category;
    private String description;
    private LocalDateTime uploadDate;
    private String notes;
}
