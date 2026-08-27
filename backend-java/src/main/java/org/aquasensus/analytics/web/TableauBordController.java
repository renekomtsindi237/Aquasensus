package org.aquasensus.analytics.web;

import java.time.Instant;
import java.util.UUID;
import org.aquasensus.analytics.application.PdfSynthese;
import org.aquasensus.analytics.application.TableauBordService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class TableauBordController {

    private final TableauBordService tableauBord;

    public TableauBordController(TableauBordService tableauBord) {
        this.tableauBord = tableauBord;
    }

    @GetMapping("/kpi")
    @PreAuthorize("hasAnyRole('PARTENAIRE','ADMIN','DELEGUE')")
    public TableauBordService.KpiAgreges kpi(
            @RequestParam(required = false) Instant debut,
            @RequestParam(required = false) Instant fin,
            @RequestParam(required = false) UUID localiteId,
            @RequestParam(required = false) UUID comiteId) {
        return tableauBord.aggregats(debut, fin, localiteId, comiteId);
    }

    @GetMapping(value = "/export", produces = "text/csv")
    @PreAuthorize("hasAnyRole('PARTENAIRE','ADMIN','DELEGUE')")
    public String exportCsv(
            @RequestParam(required = false) Instant debut,
            @RequestParam(required = false) Instant fin,
            @RequestParam(required = false) UUID localiteId,
            @RequestParam(required = false) UUID comiteId) {
        var k = tableauBord.aggregats(debut, fin, localiteId, comiteId);
        StringBuilder csv = new StringBuilder("indicateur,valeur\n");
        csv.append("retablissement_median_min,").append(n(k.retablissementMedianMinutes())).append('\n');
        csv.append("retablissement_p90_min,").append(n(k.retablissementP90Minutes())).append('\n');
        csv.append("ouvrages_suivis,").append(k.ouvragesActifsHorsHorsService()).append('\n');
        csv.append("hors_service_exclus,").append(k.horsServiceExclus()).append('\n');
        csv.append("alertes_actives,").append(k.alertesActives()).append('\n');
        csv.append("interventions_en_cours,").append(k.interventionsEnCours()).append('\n');
        csv.append("taux_anticipation,").append(k.tauxAnticipation()).append('\n');
        k.pointsParEtat().forEach((etat, n) -> csv.append("etat_").append(etat).append(',').append(n).append('\n'));
        csv.append("note,\"").append(k.note().replace("\"", "'")).append("\"\n");
        return csv.toString();
    }

    @GetMapping(value = "/export.pdf", produces = "application/pdf")
    @PreAuthorize("hasAnyRole('PARTENAIRE','ADMIN','DELEGUE')")
    public byte[] exportPdf(
            @RequestParam(required = false) Instant debut,
            @RequestParam(required = false) Instant fin,
            @RequestParam(required = false) UUID localiteId,
            @RequestParam(required = false) UUID comiteId) {
        var k = tableauBord.aggregats(debut, fin, localiteId, comiteId);
        return PdfSynthese.mensuel(
                k, debut == null ? Instant.EPOCH : debut, fin == null ? Instant.now() : fin, localiteId, comiteId);
    }

    @GetMapping("/budget")
    @PreAuthorize("hasAnyRole('PARTENAIRE','ADMIN','DELEGUE')")
    public java.util.List<TableauBordService.LigneBudget> budget(
            @RequestParam(required = false) Instant debut,
            @RequestParam(required = false) Instant fin,
            @RequestParam(required = false) UUID localiteId,
            @RequestParam(required = false) UUID comiteId) {
        return tableauBord.budget(debut, fin, localiteId, comiteId);
    }

    private static String n(Long v) {
        return v == null ? "" : v.toString();
    }
}
