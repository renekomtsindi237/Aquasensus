package org.aquasensus.shared.security;

import java.util.Arrays;
import java.util.List;
import org.aquasensus.identity.web.FiltreJwt;
import org.aquasensus.prediction.web.FiltreInterneAnalytics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class ConfigurationSecurite {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    CorsConfigurationSource cors(@Value("${aquasensus.cors.origins:http://localhost:4200}") String origines) {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(Arrays.stream(origines.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Client-Request-Id", "X-Aqs-Internal-Secret"));
        cfg.setExposedHeaders(List.of("X-Request-Id"));
        cfg.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    SecurityFilterChain chaine(HttpSecurity http, FiltreJwt filtreJwt, FiltreInterneAnalytics filtreInterne)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .headers(h -> h.contentTypeOptions(Customizer.withDefaults())
                        .frameOptions(f -> f.deny())
                        .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                        .httpStrictTransportSecurity(
                                hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                "/api/v1/health",
                                "/api/v1/auth/**",
                                "/api/docs/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health/**",
                                "/actuator/info",
                                "/actuator/metrics/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/water-points", "/api/v1/water-points/*", "/api/v1/water-points/map")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/localites")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/reports")
                        .permitAll()
                        .requestMatchers("/internal/**")
                        .hasRole("INTERNE")
                        .anyRequest()
                        .authenticated())
                .addFilterBefore(filtreInterne, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filtreJwt, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
