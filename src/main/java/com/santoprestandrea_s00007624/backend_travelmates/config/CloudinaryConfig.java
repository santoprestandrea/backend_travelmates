package com.santoprestandrea_s00007624.backend_travelmates.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione Cloudinary per gestire upload immagini
 */
@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        // Carica credenziali da .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String cloudName = dotenv.get("CLOUDINARY_CLOUD_NAME");
        String apiKey = dotenv.get("CLOUDINARY_API_KEY");
        String apiSecret = dotenv.get("CLOUDINARY_API_SECRET");

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }
}