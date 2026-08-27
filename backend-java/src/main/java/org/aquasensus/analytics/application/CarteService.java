package org.aquasensus.analytics.application;

import java.util.List;
import java.util.UUID;
import org.aquasensus.prediction.domain.AlerteRepository;
import org.aquasensus.prediction.domain.StatutAlerte;
import org.aquasensus.registry.domain.EtatPointEau;
import org.aquasensus.registry.infrastructure.PointEauEntity;
import org.aquasensus.registry.infrastructure.PointEauJpa;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarteService {

    private final PointEauJpa points;
    private final AlerteRepository alertes;

    public CarteService(PointEauJpa points, AlerteRepository alertes) {
        this.points = points;
        this.alertes = alertes;
    }

    @Transactional(readOnly = true)
    public List<MarqueurCarte> marqueurs() {
        var actives = alertes.toutes().stream()
                .filter(a -> a.statut() == StatutAlerte.ACTIVE)
                .map(a -> a.pointEauId())
                .collect(java.util.stream.Collectors.toSet());
        return points.findByActifTrue().stream().map(p -> depuis(p, actives.contains(p.getId()))).toList();
    }

    private static MarqueurCarte depuis(PointEauEntity p, boolean alerteActive) {
        EtatPointEau etat = EtatPointEau.valueOf(p.getEtat());
        return new MarqueurCarte(
                p.getId(),
                p.getCode(),
                p.getNomUsage(),
                p.getLatitude(),
                p.getLongitude(),
                etat.name(),
                etat.libelle(),
                etat.formeMarqueur(),
                p.getComiteId(),
                alerteActive);
    }

    public record MarqueurCarte(
            UUID id,
            String code,
            String nomUsage,
            java.math.BigDecimal latitude,
            java.math.BigDecimal longitude,
            String etat,
            String libelleEtat,
            String formeMarqueur,
            UUID comiteId,
            boolean alerteActive) {}
}
