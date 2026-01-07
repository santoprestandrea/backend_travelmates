package com.santoprestandrea_s00007624.backend_travelmates.service;

import com.santoprestandrea_s00007624.backend_travelmates.dto.request.ImageUploadRequest;
import com.santoprestandrea_s00007624.backend_travelmates.dto.response.ImageUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * MOCK CLOUDINARY SERVICE - Versione senza Cloudinary per testing
 */
@Service
@Slf4j
public class CloudinaryService {

    /**
     * Simula upload immagine
     */
    public ImageUploadResponse uploadImage(ImageUploadRequest request) {
        log.info("🖼️ [MOCK] Image upload simulated for folder: {}", request.getFolder());

        String mockUrl = "https://via.placeholder.com/800x600.png?text=Mock+Image";
        String mockPublicId = "travelmates/mock_" + System.currentTimeMillis();

        return ImageUploadResponse.builder()
                .imageUrl(mockUrl)
                .optimizedUrl(mockUrl)
                .thumbnailUrl(mockUrl)
                .publicId(mockPublicId)
                .fileSize(102400L)
                .format("png")
                .width(800)
                .height(600)
                .build();
    }

    /**
     * Simula eliminazione immagine
     */
    public void deleteImage(String publicId) {
        log.info("🗑️ [MOCK] Image deletion simulated for: {}", publicId);
    }

    /**
     * Genera URL ottimizzato (mock)
     */
    public String getOptimizedUrl(String publicId, Integer width, Integer height) {
        log.info("🔗 [MOCK] Optimized URL generated for: {}", publicId);
        return "https://via.placeholder.com/" + (width != null ? width : 800) + "x" + (height != null ? height : 600);
    }

    /**
     * Verifica esistenza immagine (mock)
     */
    public boolean imageExists(String publicId) {
        log.info("✅ [MOCK] Image existence check for: {}", publicId);
        return true;
    }
}