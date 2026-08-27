package org.aquasensus.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI aquasensusOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("AquaSensus API")
                        .version("v1")
                        .description(
                                "Signalement et maintenance des forages communautaires. "
                                        + "Aucun volume d'eau. Erreurs en problem+json. "
                                        + "Le groupe « interne » n'est pas destiné aux clients publics."))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    GroupedOpenApi apiPublique() {
        return GroupedOpenApi.builder().group("v1").pathsToMatch("/api/v1/**").build();
    }

    @Bean
    GroupedOpenApi apiInterne() {
        return GroupedOpenApi.builder().group("interne").pathsToMatch("/internal/**").build();
    }
}
