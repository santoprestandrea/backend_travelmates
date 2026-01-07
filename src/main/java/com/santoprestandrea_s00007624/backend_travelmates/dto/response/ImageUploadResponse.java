package com.santoprestandrea_s00007624.backend_travelmates.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Risposta dopo upload immagine su Cloudinary
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {

    /**
     * URL pubblico dell'immagine caricata
     */
    private String imageUrl;

    /**
     * URL ottimizzato (formato webp, dimensioni ridotte)
     */
    private String optimizedUrl;

    /**
     * URL thumbnail (200x200px)
     */
    private String thumbnailUrl;

    /**
     * Public ID dell'immagine su Cloudinary (per eliminazione)
     */
    private String publicId;

    /**
     * Dimensioni originali (in bytes)
     */
    private Long fileSize;

    /**
     * Formato immagine (jpg, png, webp, etc.)
     */
    private String format;

    /**
     * Larghezza in pixel
     */
    private Integer width;

    /**
     * Altezza in pixel
     */
    private Integer height;
}
