package org.aquasensus.registry.infrastructure;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.aquasensus.registry.domain.Coordonnees;
import org.aquasensus.registry.domain.EtatPointEau;
import org.aquasensus.registry.domain.FiltrePointEau;
import org.aquasensus.registry.domain.HistoriqueEtat;
import org.aquasensus.registry.domain.Localite;
import org.aquasensus.registry.domain.LocaliteRepository;
import org.aquasensus.registry.domain.PointEau;
import org.aquasensus.registry.domain.PointEauRepository;
import org.aquasensus.registry.domain.TypePointEau;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PointEauRepositoryJpa implements PointEauRepository {

    private final PointEauJpa points;
    private final HistoriqueEtatJpa historiques;
    private final LocaliteRepository localites;

    public PointEauRepositoryJpa(
            PointEauJpa points, HistoriqueEtatJpa historiques, LocaliteRepository localites) {
        this.points = points;
        this.historiques = historiques;
        this.localites = localites;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PointEau> parId(UUID id) {
        return points.findById(id).map(this::versDomaine);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PointEau> parCode(String code) {
        return points.findByCode(code).map(this::versDomaine);
    }

    @Override
    @Transactional
    public PointEau enregistrer(PointEau pointEau) {
        Instant maintenant = Instant.now();
        PointEauEntity entity = points.findById(pointEau.id()).orElseGet(PointEauEntity::new);
        if (entity.getId() == null) {
            entity.setId(pointEau.id());
            entity.setCreeLe(maintenant);
        }
        entity.setCode(pointEau.code());
        entity.setNomUsage(pointEau.nomUsage());
        entity.setType(pointEau.type().name());
        entity.setLatitude(pointEau.position().latitude());
        entity.setLongitude(pointEau.position().longitude());
        entity.setLocaliteId(pointEau.localiteId());
        entity.setComiteId(pointEau.comiteId());
        entity.setDateMiseEnService(pointEau.dateMiseEnService());
        entity.setProfondeurM(pointEau.profondeurM());
        entity.setDebitNominalLMin(pointEau.debitNominalLMin());
        entity.setPopulationDesservie(pointEau.populationDesservie());
        entity.setIntervalleMaintenanceJours(pointEau.intervalleMaintenanceJours());
        entity.setEtat(pointEau.etat().name());
        entity.setActif(pointEau.actif());
        entity.setModifieLe(maintenant);
        points.save(entity);

        Set<UUID> connus = new HashSet<>();
        historiques.findByPointEauIdOrderBySurvenuLeAsc(pointEau.id()).forEach(h -> connus.add(h.getId()));
        for (HistoriqueEtat h : pointEau.historique()) {
            if (connus.contains(h.id())) {
                continue;
            }
            HistoriqueEtatEntity he = new HistoriqueEtatEntity();
            he.setId(h.id());
            he.setPointEauId(pointEau.id());
            he.setEtatPrecedent(h.etatPrecedent() == null ? null : h.etatPrecedent().name());
            he.setEtatNouveau(h.etatNouveau().name());
            he.setMotif(h.motif());
            he.setAuteurId(h.auteurId());
            he.setSurvenuLe(h.survenuLe());
            historiques.save(he);
        }
        return pointEau;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PointEau> rechercher(FiltrePointEau filtre) {
        Set<UUID> localitesCibles = localitesFiltrees(filtre);
        Specification<PointEauEntity> spec = (root, q, cb) -> cb.conjunction();
        if (!filtre.inclureInactifs()) {
            spec = spec.and((root, q, cb) -> cb.isTrue(root.get("actif")));
        }
        if (filtre.etat() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("etat"), filtre.etat().name()));
        }
        if (filtre.comiteId() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("comiteId"), filtre.comiteId()));
        }
        if (localitesCibles != null) {
            spec = spec.and((root, q, cb) -> root.get("localiteId").in(localitesCibles));
        }
        return points.findAll(spec, PageRequest.of(filtre.page(), filtre.taille())).stream()
                .map(this::versDomaine)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PointEau> actifs() {
        return points.findByActifTrue().stream().map(this::versDomaine).toList();
    }

    private Set<UUID> localitesFiltrees(FiltrePointEau filtre) {
        if (filtre.localiteId() == null && filtre.niveauLocalite() == null) {
            return null;
        }
        List<Localite> toutes = localites.toutes();
        Set<UUID> ids = new HashSet<>();
        if (filtre.localiteId() != null) {
            ids.add(filtre.localiteId());
            ajouterDescendants(filtre.localiteId(), toutes, ids);
        }
        if (filtre.niveauLocalite() != null) {
            Set<UUID> parNiveau = new HashSet<>();
            for (Localite l : toutes) {
                if (l.niveau() == filtre.niveauLocalite()) {
                    parNiveau.add(l.id());
                }
            }
            if (ids.isEmpty()) {
                return parNiveau;
            }
            ids.retainAll(parNiveau);
        }
        return ids;
    }

    private void ajouterDescendants(UUID parent, List<Localite> toutes, Set<UUID> acc) {
        for (Localite l : toutes) {
            if (parent.equals(l.parentId()) && acc.add(l.id())) {
                ajouterDescendants(l.id(), toutes, acc);
            }
        }
    }

    private PointEau versDomaine(PointEauEntity e) {
        List<HistoriqueEtat> hist = new ArrayList<>();
        for (HistoriqueEtatEntity h : historiques.findByPointEauIdOrderBySurvenuLeAsc(e.getId())) {
            hist.add(new HistoriqueEtat(
                    h.getId(),
                    h.getEtatPrecedent() == null ? null : EtatPointEau.valueOf(h.getEtatPrecedent()),
                    EtatPointEau.valueOf(h.getEtatNouveau()),
                    h.getMotif(),
                    h.getAuteurId(),
                    h.getSurvenuLe()));
        }
        return new PointEau(
                e.getId(),
                e.getCode(),
                e.getNomUsage(),
                TypePointEau.valueOf(e.getType()),
                new Coordonnees(e.getLatitude(), e.getLongitude()),
                e.getLocaliteId(),
                e.getComiteId(),
                e.getDateMiseEnService(),
                e.getProfondeurM(),
                e.getDebitNominalLMin(),
                e.getPopulationDesservie(),
                e.getIntervalleMaintenanceJours(),
                EtatPointEau.valueOf(e.getEtat()),
                e.isActif(),
                hist);
    }
}
