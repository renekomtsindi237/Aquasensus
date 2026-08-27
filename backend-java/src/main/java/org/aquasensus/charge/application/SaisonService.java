package org.aquasensus.charge.application;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.aquasensus.charge.domain.CalendrierSaisonRepository;
import org.aquasensus.charge.domain.PeriodeSaison;
import org.aquasensus.shared.error.RegleMetierException;
import org.aquasensus.shared.error.RessourceIntrouvableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaisonService {

    private final CalendrierSaisonRepository saisons;

    public SaisonService(CalendrierSaisonRepository saisons) {
        this.saisons = saisons;
    }

    public List<PeriodeSaison> lister() {
        return saisons.toutes();
    }

    @Transactional
    public PeriodeSaison enregistrer(PeriodeSaison candidate) {
        if (candidate.jourDebut() < 1 || candidate.jourDebut() > 366
                || candidate.jourFin() < 1 || candidate.jourFin() > 366) {
            throw new RegleMetierException("EF-31", "Les jours de saison doivent être entre 1 et 366.");
        }
        if (candidate.coefficient() <= 0) {
            throw new RegleMetierException("EF-31", "Le coefficient saisonnier doit être strictement positif.");
        }
        garantirSansChevauchement(candidate);
        return saisons.enregistrer(candidate);
    }

    @Transactional
    public void desactiver(UUID id) {
        saisons.parId(id).orElseThrow(RessourceIntrouvableException::new);
        saisons.supprimer(id);
    }

    private void garantirSansChevauchement(PeriodeSaison candidate) {
        for (PeriodeSaison autre : saisons.toutes()) {
            if (!autre.actif() || autre.id().equals(candidate.id())) {
                continue;
            }
            if (!Objects.equals(autre.localiteId(), candidate.localiteId())) {
                continue;
            }
            for (int j = 1; j <= 366; j++) {
                if (candidate.couvre(j) && autre.couvre(j)) {
                    throw new RegleMetierException(
                            "EF-31", "Les périodes saisonnières d'une même localité ne doivent pas se chevaucher.");
                }
            }
        }
    }
}
