package com.santoprestandrea_s00007624.backend_travelmates;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendTravelmatesApplication {

    public static void main(String[] args) {
        // Carica il file .env
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .load();

            // Imposta solo le variabili dal file .env (non quelle di sistema)
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });

        } catch (Exception e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
        }

        // Avvia Spring Boot
        SpringApplication.run(BackendTravelmatesApplication.class, args);
    }

}
