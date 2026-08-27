package org.aquasensus.audit.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.aquasensus.audit.application.JournalAuditService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final JournalAuditService journal;

    public AuditController(JournalAuditService journal) {
        this.journal = journal;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<JournalAuditService.Entree> lister(
            @RequestParam(required = false) String entite,
            @RequestParam(required = false) UUID acteurId,
            @RequestParam(required = false) Instant debut,
            @RequestParam(required = false) Instant fin) {
        return journal.lister(entite, acteurId, debut, fin);
    }
}
