package org.aquasensus;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * ISS-003 : migrations Flyway sur PostgreSQL éphémère (Docker).
 * Désactivé par défaut ; lancer avec AQS_IT_POSTGRES=1.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "AQS_IT_POSTGRES", matches = "1")
class FlywayPostgresIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void schemaIdentiteEtReferentielPresent() {
        Integer roles = jdbc.queryForObject("SELECT COUNT(*) FROM role", Integer.class);
        assertThat(roles).isEqualTo(5);
        Integer tables = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name IN ('localite','comite','utilisateur')",
                Integer.class);
        assertThat(tables).isEqualTo(3);
    }
}
