package org.aquasensus.prediction.web;

import java.util.List;
import org.aquasensus.prediction.application.ExtractionAnalyticsService;
import org.aquasensus.prediction.application.PublicationAnalyticsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/internal/analytics")
@PreAuthorize("hasRole('INTERNE')")
public class AnalyticsInterneController {

    private final ExtractionAnalyticsService extraction;
    private final PublicationAnalyticsService publication;

    public AnalyticsInterneController(
            ExtractionAnalyticsService extraction, PublicationAnalyticsService publication) {
        this.extraction = extraction;
        this.publication = publication;
    }

    @GetMapping("/dataset")
    public ExtractionAnalyticsService.JeuDonnees dataset() {
        return extraction.extraire();
    }

    @PostMapping("/health-scores")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PublicationCompte healthScores(@RequestBody List<PublicationAnalyticsService.IndicePublie> lots) {
        return new PublicationCompte(publication.publierIndices(lots));
    }

    @PostMapping("/alerts")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PublicationCompte alerts(@RequestBody List<PublicationAnalyticsService.AlertePubliee> lots) {
        return new PublicationCompte(publication.publierAlertes(lots));
    }

    public record PublicationCompte(int enregistres) {}
}
