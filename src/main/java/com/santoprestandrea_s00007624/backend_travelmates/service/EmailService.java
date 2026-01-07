package com.santoprestandrea_s00007624.backend_travelmates.service;

import com.santoprestandrea_s00007624.backend_travelmates.entity.Settlement;
import com.santoprestandrea_s00007624.backend_travelmates.entity.SharedExpense;
import com.santoprestandrea_s00007624.backend_travelmates.entity.Trip;
import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * MOCK EMAIL SERVICE - Versione senza Mailgun per testing
 */
@Service
@Slf4j
public class EmailService {

    /**
     * Simula invio email di benvenuto
     */
    public void sendWelcomeEmail(User user) {
        log.info("📧 [MOCK] Welcome email would be sent to: {}", user.getEmail());
        log.info("   Subject: Benvenuto su TravelMates! 🌍");
    }

    /**
     * Simula invio email di invito al viaggio
     */
    public void sendTripInvitationEmail(User invitedUser, Trip trip, User inviter) {
        log.info("📧 [MOCK] Trip invitation email would be sent to: {}", invitedUser.getEmail());
        log.info("   Subject: Sei stato invitato a {} 🎉", trip.getTitle());
        log.info("   From: {}", inviter.getEmail());
    }

    /**
     * Simula notifica di nuova spesa condivisa
     */
    public void sendExpenseNotificationEmail(SharedExpense expense, User member) {
        log.info("📧 [MOCK] Expense notification would be sent to: {}", member.getEmail());
        log.info("   Subject: Nuova spesa aggiunta: {}", expense.getDescription());
    }

    /**
     * Simula richiesta di pagamento
     */
    public void sendSettlementRequestEmail(Settlement settlement) {
        log.info("📧 [MOCK] Settlement request would be sent to: {}", settlement.getFromUser().getEmail());
        log.info("   Subject: Richiesta di pagamento per {}", settlement.getTrip().getTitle());
    }

    /**
     * Simula conferma di pagamento ricevuto
     */
    public void sendSettlementCompletedEmail(Settlement settlement) {
        log.info("📧 [MOCK] Settlement completed email would be sent to: {}", settlement.getToUser().getEmail());
        log.info("   Subject: Pagamento ricevuto - {} ✅", settlement.getTrip().getTitle());
    }
}