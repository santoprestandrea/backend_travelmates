package com.santoprestandrea_s00007624.backend_travelmates.controller;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UploadDocumentRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.DocumentResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.DocumentCategory;
import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import com.santoprestandrea_s00007624.backend_travelmates.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLLER: DOCUMENT MANAGEMENT
 *
 * REST API for managing trip documents.
 *
 * BASE URL: /api/trips/{tripId}/documents
 */
@RestController
@RequestMapping("/api/trips/{tripId}/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    // ============================================
    // UPLOAD DOCUMENT
    // ============================================

    /**
     * POST /api/trips/{tripId}/documents
     * Upload a new document to a trip
     * All trip members can upload
     */
    @PostMapping
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Long tripId,
            @Valid @RequestBody UploadDocumentRequest request,
            @AuthenticationPrincipal User currentUser) {
        DocumentResponse document = documentService.uploadDocument(tripId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    // ============================================
    // DELETE DOCUMENT
    // ============================================

    /**
     * DELETE /api/trips/{tripId}/documents/{documentId}
     * Delete a document
     * Only uploader or ORGANIZER can delete
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Map<String, String>> deleteDocument(
            @PathVariable Long tripId,
            @PathVariable Long documentId,
            @AuthenticationPrincipal User currentUser) {
        documentService.deleteDocument(tripId, documentId, currentUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Document deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ============================================
    // GET DOCUMENTS
    // ============================================

    /**
     * GET /api/trips/{tripId}/documents
     * Get all documents for a trip
     * Query params:
     * - category: Filter by category (optional)
     */
    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getDocuments(
            @PathVariable Long tripId,
            @RequestParam(required = false) DocumentCategory category,
            @AuthenticationPrincipal User currentUser) {
        List<DocumentResponse> documents = documentService.getDocumentsByTrip(tripId, currentUser, category);
        return ResponseEntity.ok(documents);
    }

    /**
     * GET /api/trips/{tripId}/documents/{documentId}
     * Get a single document by ID
     */
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getDocumentById(
            @PathVariable Long tripId,
            @PathVariable Long documentId,
            @AuthenticationPrincipal User currentUser) {
        DocumentResponse document = documentService.getDocumentById(tripId, documentId, currentUser);
        return ResponseEntity.ok(document);
    }

    /**
     * GET /api/trips/{tripId}/documents/my
     * Get documents uploaded by current user
     */
    @GetMapping("/my")
    public ResponseEntity<List<DocumentResponse>> getMyDocuments(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User currentUser) {
        List<DocumentResponse> documents = documentService.getMyDocuments(tripId, currentUser);
        return ResponseEntity.ok(documents);
    }

    /**
     * GET /api/trips/{tripId}/documents/search
     * Search documents by file name
     * Query params:
     * - keyword: Search keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<DocumentResponse>> searchDocuments(
            @PathVariable Long tripId,
            @RequestParam String keyword,
            @AuthenticationPrincipal User currentUser) {
        List<DocumentResponse> documents = documentService.searchDocuments(tripId, keyword, currentUser);
        return ResponseEntity.ok(documents);
    }
}
