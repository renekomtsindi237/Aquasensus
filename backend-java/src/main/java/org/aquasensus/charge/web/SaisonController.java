package org.aquasensus.charge.web;

import java.util.List;
import java.util.UUID;
import org.aquasensus.charge.application.SaisonService;
import org.aquasensus.charge.domain.PeriodeSaison;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seasons")
public class SaisonController {

    private final SaisonService saisons;

    public SaisonController(SaisonService saisons) {
        this.saisons = saisons;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<PeriodeSaison> lister() {
        return saisons.lister();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public PeriodeSaison creer(@RequestBody PeriodeSaison periode) {
        PeriodeSaison aEnregistrer = new PeriodeSaison(
                periode.id() == null ? UUID.randomUUID() : periode.id(),
                periode.localiteId(),
                periode.libelle(),
                periode.jourDebut(),
                periode.jourFin(),
                periode.coefficient(),
                periode.actif());
        return saisons.enregistrer(aEnregistrer);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PeriodeSaison modifier(@PathVariable UUID id, @RequestBody PeriodeSaison periode) {
        return saisons.enregistrer(new PeriodeSaison(
                id,
                periode.localiteId(),
                periode.libelle(),
                periode.jourDebut(),
                periode.jourFin(),
                periode.coefficient(),
                periode.actif()));
    }
}
