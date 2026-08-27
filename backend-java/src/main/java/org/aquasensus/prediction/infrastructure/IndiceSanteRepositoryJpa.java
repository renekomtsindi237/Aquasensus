package org.aquasensus.prediction.infrastructure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.aquasensus.prediction.domain.IndiceSante;
import org.aquasensus.prediction.domain.IndiceSanteRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class IndiceSanteRepositoryJpa implements IndiceSanteRepository {

    private final IndiceSanteJpa jpa;

    public IndiceSanteRepositoryJpa(IndiceSanteJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<IndiceSante> dernier(UUID pointEauId) {
        return jpa.findFirstByPointEauIdOrderByDateCalculDesc(pointEauId).map(this::versDomaine);
    }

    @Override
    public Optional<IndiceSante> parOuvrageEtDate(UUID pointEauId, LocalDate date) {
        return jpa.findByPointEauIdAndDateCalcul(pointEauId, date).map(this::versDomaine);
    }

    @Override
    @Transactional
    public IndiceSante enregistrer(IndiceSante indice) {
        IndiceSanteEntity e = jpa.findByPointEauIdAndDateCalcul(indice.pointEauId(), indice.dateCalcul())
                .orElseGet(IndiceSanteEntity::new);
        if (e.getId() == null) {
            e.setId(indice.id() == null ? UUID.randomUUID() : indice.id());
        }
        e.setPointEauId(indice.pointEauId());
        e.setDateCalcul(indice.dateCalcul());
        e.setScore(BigDecimal.valueOf(indice.score()).setScale(2, RoundingMode.HALF_UP));
        e.setBande(indice.bande());
        e.setConfiance(indice.confiance());
        e.setChargeCumuleeJours(dec(indice.chargeCumuleeJours(), 2));
        e.setIntervalleEffectifJours(indice.intervalleEffectifJours());
        e.setIndicateurM(dec(indice.indicateurM(), 4));
        e.setIndicateurP(dec(indice.indicateurP(), 4));
        e.setIndicateurS(dec(indice.indicateurS(), 4));
        e.setIndicateurT(dec(indice.indicateurT(), 4));
        e.setFacteurs(indice.facteurs());
        e.setVersionParametrage(indice.versionParametrage());
        jpa.save(e);
        return versDomaine(e);
    }

    private static BigDecimal dec(Double v, int scale) {
        return v == null ? null : BigDecimal.valueOf(v).setScale(scale, RoundingMode.HALF_UP);
    }

    private IndiceSante versDomaine(IndiceSanteEntity e) {
        return new IndiceSante(
                e.getId(),
                e.getPointEauId(),
                e.getDateCalcul(),
                e.getScore().doubleValue(),
                e.getBande(),
                e.getConfiance(),
                e.getChargeCumuleeJours() == null ? null : e.getChargeCumuleeJours().doubleValue(),
                e.getIntervalleEffectifJours(),
                e.getIndicateurM() == null ? null : e.getIndicateurM().doubleValue(),
                e.getIndicateurP() == null ? null : e.getIndicateurP().doubleValue(),
                e.getIndicateurS() == null ? null : e.getIndicateurS().doubleValue(),
                e.getIndicateurT() == null ? null : e.getIndicateurT().doubleValue(),
                e.getFacteurs(),
                e.getVersionParametrage());
    }
}
