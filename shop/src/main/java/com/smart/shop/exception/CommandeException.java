package com.smart.shop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommandeException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public CommandeException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    // Exceptions spécifiques
    public static class StockInsuffisantException extends CommandeException {
        public StockInsuffisantException(String message) {
            super(message, "STOCK_INSUFFISANT", HttpStatus.BAD_REQUEST);
        }
    }

    public static class CommandeNonTrouveeException extends CommandeException {
        public CommandeNonTrouveeException(String id) {
            super("Commande non trouvée avec l'ID: " + id, "COMMANDE_NON_TROUVEE", HttpStatus.NOT_FOUND);
        }
    }

    public static class CommandeDejaTraiteeException extends CommandeException {
        public CommandeDejaTraiteeException(String id, String statutActuel) {
            super("La commande " + id + " est déjà " + statutActuel, 
                  "COMMANDE_DEJA_TRAITEE", HttpStatus.CONFLICT);
        }
    }
}