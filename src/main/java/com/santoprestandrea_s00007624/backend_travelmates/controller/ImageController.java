package com.santoprestandrea_s00007624.backend_travelmates.controller;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.ImageUploadRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.ImageUploadResponse;
import com.santoprestandrea_s00007624.backend_travelmates.service.CloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST per gestione upload immagini tramite Cloudinary
 */
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final CloudinaryService cloudinaryService;

    /**
     * Upload immagine generica su Cloudinary
     *
     * @param request Contiene immagine Base64 e folder opzionale
     * @return Dettagli dell'immagine caricata con URL
     */
    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponse> uploadImage(@Valid @RequestBody ImageUploadRequest request) {
        ImageUploadResponse response = cloudinaryService.uploadImage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload immagine profilo utente
     *
     * @param request Contiene immagine Base64
     * @return Dettagli dell'immagine caricata
     */
    @PostMapping("/upload/profile")
    public ResponseEntity<ImageUploadResponse> uploadProfileImage(@Valid @RequestBody ImageUploadRequest request) {
        request.setFolder("users/profiles");
        ImageUploadResponse response = cloudinaryService.uploadImage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload immagine copertina viaggio
     *
     * @param request Contiene immagine Base64
     * @return Dettagli dell'immagine caricata
     */
    @PostMapping("/upload/trip-cover")
    public ResponseEntity<ImageUploadResponse> uploadTripCoverImage(@Valid @RequestBody ImageUploadRequest request) {
        request.setFolder("trips/covers");
        ImageUploadResponse response = cloudinaryService.uploadImage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload immagine ricevuta spesa
     *
     * @param request Contiene immagine Base64
     * @return Dettagli dell'immagine caricata
     */
    @PostMapping("/upload/receipt")
    public ResponseEntity<ImageUploadResponse> uploadReceiptImage(@Valid @RequestBody ImageUploadRequest request) {
        request.setFolder("expenses/receipts");
        ImageUploadResponse response = cloudinaryService.uploadImage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload immagine documento
     *
     * @param request Contiene immagine Base64
     * @return Dettagli dell'immagine caricata
     */
    @PostMapping("/upload/document")
    public ResponseEntity<ImageUploadResponse> uploadDocumentImage(@Valid @RequestBody ImageUploadRequest request) {
        request.setFolder("documents");
        ImageUploadResponse response = cloudinaryService.uploadImage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina immagine da Cloudinary
     *
     * @param publicId Public ID dell'immagine su Cloudinary
     * @return 204 No Content
     */
    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteImage(@PathVariable String publicId) {
        // Sostituisci '___' con '/' nel publicId (problema con path variables)
        String decodedPublicId = publicId.replace("___", "/");
        cloudinaryService.deleteImage(decodedPublicId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ottieni URL ottimizzato per un'immagine esistente
     *
     * @param publicId Public ID dell'immagine
     * @param width    Larghezza desiderata (opzionale)
     * @param height   Altezza desiderata (opzionale)
     * @return URL ottimizzato
     */
    @GetMapping("/optimized/{publicId}")
    public ResponseEntity<String> getOptimizedUrl(
            @PathVariable String publicId,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height) {
        String decodedPublicId = publicId.replace("___", "/");
        String optimizedUrl = cloudinaryService.getOptimizedUrl(decodedPublicId, width, height);
        return ResponseEntity.ok(optimizedUrl);
    }
}
