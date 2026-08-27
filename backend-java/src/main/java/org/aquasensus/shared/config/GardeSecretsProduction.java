package org.aquasensus.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class GardeSecretsProduction implements ApplicationRunner {

    private final String jwt;
    private final String interne;

    public GardeSecretsProduction(
            @Value("${aquasensus.jwt.secret}") String jwt,
            @Value("${aquasensus.interne.secret}") String interne) {
        this.jwt = jwt;
        this.interne = interne;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (jwt == null
                || jwt.length() < 32
                || jwt.contains("dev-only")
                || jwt.contains("change-me")
                || interne == null
                || interne.contains("dev-internal")
                || interne.contains("change-me")) {
            throw new IllegalStateException(
                    "Profil prod : AQS_JWT_SECRET et AQS_INTERNAL_SECRET doivent être des secrets dédiés (ISS-061).");
        }
    }
}
