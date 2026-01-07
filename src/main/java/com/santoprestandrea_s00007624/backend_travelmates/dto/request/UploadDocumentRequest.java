package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import com.santoprestandrea_s00007624.backend_travelmates.entity.DocumentCategory;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO - UPLOAD DOCUMENT REQUEST
 *
 * Used to upload a new document to a trip.
 *
 * JSON EXAMPLE:
 * {
 * "fileName": "flight-ticket-rome.pdf",
 * "fileUrl": "https://cloudinary.com/files/abc123.pdf",
 * "fileType": "application/pdf",
 * "fileSize": 2048576,
 * "category": "TICKET",
 * "description": "Flight ticket from Paris to Rome",
 * "notes": "Remember to print this"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadDocumentRequest {

    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name too long")
    private String fileName;

    @NotBlank(message = "File URL is required")
    @Size(max = 500, message = "File URL too long")
    private String fileUrl;

    @Size(max = 100, message = "File type too long")
    private String fileType;

    @Min(value = 0, message = "File size cannot be negative")
    private Long fileSize;

    @NotNull(message = "Category is required")
    private DocumentCategory category;

    @Size(max = 500, message = "Description too long")
    private String description;

    @Size(max = 1000, message = "Notes too long")
    private String notes;
}
