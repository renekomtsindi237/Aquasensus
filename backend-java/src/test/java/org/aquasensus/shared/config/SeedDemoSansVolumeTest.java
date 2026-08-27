package org.aquasensus.shared.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class SeedDemoSansVolumeTest {

    @Test
    void seedSansLitreNiBidonEtIdentifiantsStables() throws Exception {
        String sql = new ClassPathResource("db/demo/R__seed_demo.sql")
                .getContentAsString(StandardCharsets.UTF_8)
                .toLowerCase();
        assertThat(sql).doesNotContain("litre", "bidon", "volume_eau");
        assertThat(sql).contains("d0000001-0000-4000-8000-000000000001");
        assertThat(sql).contains("yde-d01");
    }
}
