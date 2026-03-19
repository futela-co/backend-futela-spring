package com.futela.api.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Configuration de seed data pour le profil dev.
 * <p>
 * Les fixtures principales sont gérées par la migration Flyway V009__seed_fixtures.sql.
 * Cette classe vérifie au démarrage que les données de test sont bien présentes
 * et log un résumé des données disponibles.
 * </p>
 */
@Configuration
@Profile("dev")
public class DataSeederConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DataSeederConfiguration.class);

    @Bean
    public CommandLineRunner dataSeederRunner(JdbcTemplate jdbcTemplate) {
        return args -> {
            log.info("=== Futela Dev Data Seeder ===");
            log.info("Vérification des données de test...");

            logTableCount(jdbcTemplate, "companies", "Entreprises");
            logTableCount(jdbcTemplate, "users", "Utilisateurs");
            logTableCount(jdbcTemplate, "countries", "Pays");
            logTableCount(jdbcTemplate, "provinces", "Provinces");
            logTableCount(jdbcTemplate, "cities", "Villes");
            logTableCount(jdbcTemplate, "towns", "Communes");
            logTableCount(jdbcTemplate, "districts", "Quartiers");
            logTableCount(jdbcTemplate, "addresses", "Adresses");
            logTableCount(jdbcTemplate, "categories", "Catégories");
            logTableCount(jdbcTemplate, "properties", "Propriétés");
            logTableCount(jdbcTemplate, "photos", "Photos");
            logTableCount(jdbcTemplate, "currencies", "Devises");
            logTableCount(jdbcTemplate, "payment_methods", "Moyens de paiement");
            logTableCount(jdbcTemplate, "leases", "Baux");
            logTableCount(jdbcTemplate, "rent_invoices", "Factures loyer");
            logTableCount(jdbcTemplate, "rent_payments", "Paiements loyer");
            logTableCount(jdbcTemplate, "reservations", "Réservations");
            logTableCount(jdbcTemplate, "reviews", "Avis");
            logTableCount(jdbcTemplate, "conversations", "Conversations");
            logTableCount(jdbcTemplate, "messages", "Messages");
            logTableCount(jdbcTemplate, "notifications", "Notifications");
            logTableCount(jdbcTemplate, "transactions", "Transactions");

            logTestAccounts();

            log.info("=== Seed data check terminé ===");
        };
    }

    private void logTableCount(JdbcTemplate jdbcTemplate, String tableName, String label) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM " + tableName + " WHERE deleted_at IS NULL",
                    Integer.class
            );
            log.info("  {} : {} enregistrement(s)", label, count);
        } catch (Exception e) {
            log.warn("  {} : table '{}' non accessible - {}", label, tableName, e.getMessage());
        }
    }

    private void logTestAccounts() {
        log.info("");
        log.info("=== Comptes de test (mot de passe: password123) ===");
        log.info("  SUPER_ADMIN : superadmin@futela.com");
        log.info("  ADMIN       : admin@futela.com (Futela Immo)");
        log.info("  ADMIN       : admin@kinproperties.cd (Kinshasa Properties)");
        log.info("  OWNER       : pierre.mbuyi@futela.com");
        log.info("  OWNER       : grace.kabongo@futela.com");
        log.info("  TENANT      : jean.tshimanga@futela.com");
        log.info("  TENANT      : marie.nseka@futela.com");
        log.info("  USER        : patrick.kalala@futela.com");
        log.info("  USER        : esther.lunda@futela.com");
    }
}
