package com.santoprestandrea_s00007624.backend_travelmates.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per upload immagini tramite Cloudinary
 * Accetta l'immagine in formato Base64
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadRequest {

    /**
     * Immagine codificata in Base64
     * Esempio: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUg..."
     */
    @NotBlank(message = "Image data is required")
    private String imageBase64;

    /**
     * Cartella opzionale in Cloudinary dove salvare l'immagine
     * Esempi: "users", "trips", "expenses", "documents"
     */
    private String folder;
}
