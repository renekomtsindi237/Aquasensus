package org.aquasensus.registry.application;

import java.math.BigDecimal;
import org.aquasensus.registry.domain.EmpriseGeographique;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationEmprise {

    @Bean
    EmpriseGeographique empriseCameroun(
            @Value("${aquasensus.emprise.lat-min}") BigDecimal latMin,
            @Value("${aquasensus.emprise.lat-max}") BigDecimal latMax,
            @Value("${aquasensus.emprise.lon-min}") BigDecimal lonMin,
            @Value("${aquasensus.emprise.lon-max}") BigDecimal lonMax) {
        return new EmpriseGeographique(latMin, latMax, lonMin, lonMax);
    }
}
