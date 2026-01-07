package com.santoprestandrea_s00007624.backend_travelmates.mapper;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UploadDocumentRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.DocumentResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MAPPER: DOCUMENT ↔ DTO
 *
 * Converts between Document entities and DTOs.
 */
@Component
public class DocumentMapper {

    @Autowired
    private UserMapper userMapper;

    /**
     * Converts Document entity to DocumentResponse DTO
     */
    public DocumentResponse toResponse(Document document) {
        if (document == null) {
            return null;
        }

        return DocumentResponse.builder()
                .id(document.getId())
                .tripId(document.getTrip().getId())
                .uploadedBy(userMapper.toResponse(document.getUploadedBy()))
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .fileType(document.getFileType())
                .fileSize(document.getFileSize())
                .category(document.getCategory())
                .description(document.getDescription())
                .uploadDate(document.getUploadDate())
                .notes(document.getNotes())
                .build();
    }

    /**
     * Converts UploadDocumentRequest to Document entity
     */
    public Document toEntity(UploadDocumentRequest request) {
        if (request == null) {
            return null;
        }

        return Document.builder()
                .fileName(request.getFileName())
                .fileUrl(request.getFileUrl())
                .fileType(request.getFileType())
                .fileSize(request.getFileSize())
                .category(request.getCategory())
                .description(request.getDescription())
                .notes(request.getNotes())
                .build();
    }
}
