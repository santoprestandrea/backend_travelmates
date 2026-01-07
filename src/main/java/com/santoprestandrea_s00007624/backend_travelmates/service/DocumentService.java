package com.santoprestandrea_s00007624.backend_travelmates.service;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.UploadDocumentRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.DocumentResponse;
import com.santoprestandrea_s00007624.backend_travelmates.entity.*;
import com.santoprestandrea_s00007624.backend_travelmates.exception.ResourceNotFoundException;
import com.santoprestandrea_s00007624.backend_travelmates.exception.UnauthorizedException;
import com.santoprestandrea_s00007624.backend_travelmates.mapper.DocumentMapper;
import com.santoprestandrea_s00007624.backend_travelmates.repository.DocumentRepository;
import com.santoprestandrea_s00007624.backend_travelmates.repository.TripMemberRepository;
import com.santoprestandrea_s00007624.backend_travelmates.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICE: DOCUMENT MANAGEMENT
 *
 * Handles business logic for trip documents.
 */
@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripMemberRepository tripMemberRepository;

    @Autowired
    private DocumentMapper documentMapper;

    // ============================================
    // UPLOAD DOCUMENT
    // ============================================

    /**
     * Upload a new document to a trip
     * All trip members can upload documents
     */
    public DocumentResponse uploadDocument(Long tripId, UploadDocumentRequest request, User currentUser) {
        // 1. Check if trip exists
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // 2. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 3. Create document
        Document document = documentMapper.toEntity(request);
        document.setTrip(trip);
        document.setUploadedBy(currentUser);

        // 4. Save and return
        Document savedDocument = documentRepository.save(document);
        return documentMapper.toResponse(savedDocument);
    }

    // ============================================
    // DELETE DOCUMENT
    // ============================================

    /**
     * Delete a document
     * Only the uploader or ORGANIZER can delete
     */
    public void deleteDocument(Long tripId, Long documentId, User currentUser) {
        // 1. Check if document exists and belongs to the trip
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        if (!document.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Document does not belong to this trip");
        }

        // 2. Check if user is the uploader or ORGANIZER
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        boolean isUploader = document.getUploadedBy().getId().equals(currentUser.getId());
        boolean isOrganizer = membership.getRole() == MemberRole.ORGANIZER;

        if (!isUploader && !isOrganizer) {
            throw new UnauthorizedException("Only the uploader or organizers can delete this document");
        }

        // 3. Delete document
        documentRepository.delete(document);
    }

    // ============================================
    // GET DOCUMENTS
    // ============================================

    /**
     * Get all documents for a trip
     * All trip members can view documents
     */
    public List<DocumentResponse> getDocumentsByTrip(Long tripId, User currentUser, DocumentCategory category) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get documents based on filters
        List<Document> documents;

        if (category != null) {
            // Get documents by category
            documents = documentRepository.findByTrip_IdAndCategoryOrderByUploadDateDesc(tripId, category);
        } else {
            // Get all documents
            documents = documentRepository.findByTrip_IdOrderByUploadDateDesc(tripId);
        }

        // 3. Convert to response
        return documents.stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a single document by ID
     */
    public DocumentResponse getDocumentById(Long tripId, Long documentId, User currentUser) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get document
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        // 3. Verify document belongs to trip
        if (!document.getTrip().getId().equals(tripId)) {
            throw new UnauthorizedException("Document does not belong to this trip");
        }

        // 4. Return response
        return documentMapper.toResponse(document);
    }

    /**
     * Get documents uploaded by current user
     */
    public List<DocumentResponse> getMyDocuments(Long tripId, User currentUser) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Get user's documents
        List<Document> documents = documentRepository.findByTrip_IdAndUploadedBy_IdOrderByUploadDateDesc(
                tripId, currentUser.getId());

        // 3. Convert to response
        return documents.stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search documents by file name
     */
    public List<DocumentResponse> searchDocuments(Long tripId, String keyword, User currentUser) {
        // 1. Check if user is a member
        TripMember membership = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("You are not a member of this trip"));

        // 2. Search documents
        List<Document> documents = documentRepository.searchByFileName(tripId, keyword);

        // 3. Convert to response
        return documents.stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
