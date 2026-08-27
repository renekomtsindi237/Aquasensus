package org.aquasensus.shared.web;

import java.time.Instant;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SanteController {

    private final DataSource dataSource;

    public SanteController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> sante() throws Exception {
        try (var c = dataSource.getConnection()) {
            c.createStatement().execute("SELECT 1");
        }
        return ResponseEntity.ok(Map.of("statut", "ok", "horodatage", Instant.now().toString()));
    }
}
