package com.santoprestandrea_s00007624.backend_travelmates.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione Mailgun per invio email
 */
@Configuration
@Getter
public class MailgunConfig {

    private final String apiKey;
    private final String domain;
    private final String apiBaseUrl;
    private final String fromEmail;
    private final String fromName;

    public MailgunConfig() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        this.apiKey = dotenv.get("MAILGUN_API_KEY");
        this.domain = dotenv.get("MAILGUN_DOMAIN");
        this.apiBaseUrl = "https://api.mailgun.net/v3/" + domain + "/messages";
        this.fromEmail = dotenv.get("MAILGUN_FROM_EMAIL", "noreply@travelmates.com");
        this.fromName = dotenv.get("MAILGUN_FROM_NAME", "TravelMates");
    }

    public String getFrom() {
        return fromName + " <" + fromEmail + ">";
    }
}
