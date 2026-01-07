package com.santoprestandrea_s00007624.backend_travelmates.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * DOCUMENT ENTITY - Represents a shared document in a trip
 *
 * Documents are files shared among trip members.
 * Examples: tickets, booking confirmations, passports, maps, receipts.
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * TRIP ASSOCIATION
     * Each document belongs to a specific trip
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    /**
     * UPLOADER
     * User who uploaded this document
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    /**
     * FILE INFORMATION
     */
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "file_type", length = 100)
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize; // in bytes

    /**
     * CATEGORY
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DocumentCategory category;

    /**
     * DESCRIPTION
     */
    @Column(length = 500)
    private String description;

    /**
     * TIMESTAMPS
     */
    @CreationTimestamp
    @Column(name = "upload_date", nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    /**
     * NOTES
     */
    @Column(length = 1000)
    private String notes;
}
