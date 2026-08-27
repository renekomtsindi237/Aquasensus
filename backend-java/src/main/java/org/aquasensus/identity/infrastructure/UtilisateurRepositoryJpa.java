package org.aquasensus.identity.infrastructure;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.StatutCompte;
import org.aquasensus.identity.domain.Utilisateur;
import org.aquasensus.identity.domain.UtilisateurRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

@Repository
public class UtilisateurRepositoryJpa implements UtilisateurRepository {

    private final UtilisateurJpa utilisateurs;
    private final UtilisateurRoleJpa roles;
    private final UtilisateurPerimetreJpa perimetres;
    private final EntityManager entityManager;

    public UtilisateurRepositoryJpa(
            UtilisateurJpa utilisateurs,
            UtilisateurRoleJpa roles,
            UtilisateurPerimetreJpa perimetres,
            EntityManager entityManager) {
        this.utilisateurs = utilisateurs;
        this.roles = roles;
        this.perimetres = perimetres;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Utilisateur> parIdentifiant(String identifiant) {
        return utilisateurs.findByIdentifiant(identifiant).map(this::versDomaine);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Utilisateur> parId(java.util.UUID id) {
        return utilisateurs.findById(id).map(this::versDomaine);
    }

    @Override
    @Transactional
    public Utilisateur enregistrer(Utilisateur utilisateur) {
        UtilisateurEntity entity = utilisateurs.findById(utilisateur.id()).orElseGet(UtilisateurEntity::new);
        Instant maintenant = Instant.now();
        if (entity.getId() == null) {
            entity.setId(utilisateur.id());
            entity.setCreeLe(maintenant);
        }
        entity.setIdentifiant(utilisateur.identifiant());
        entity.setMotDePasseHache(utilisateur.motDePasseHache());
        entity.setNomAffichage(utilisateur.nomAffichage());
        entity.setStatut(utilisateur.statut().name());
        entity.setEchecsConsecutifs(utilisateur.echecsConsecutifs());
        entity.setVerrouilleJusqua(utilisateur.verrouilleJusqua());
        entity.setDoitChangerMotDePasse(utilisateur.doitChangerMotDePasse());
        entity.setModifieLe(maintenant);
        utilisateurs.save(entity);
        entityManager.flush();

        roles.deleteByUtilisateurId(utilisateur.id());
        entityManager.flush();
        for (CodeRole role : utilisateur.roles()) {
            roles.save(new UtilisateurRoleEntity(utilisateur.id(), role.name()));
        }
        entityManager.flush();
        perimetres.deleteByUtilisateurId(utilisateur.id());
        entityManager.flush();
        for (java.util.UUID comiteId : utilisateur.comitesPerimetre()) {
            perimetres.save(new UtilisateurPerimetreEntity(utilisateur.id(), comiteId));
        }
        entityManager.flush();
        return utilisateur;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<Utilisateur> lister() {
        return utilisateurs.findAll().stream().map(this::versDomaine).toList();
    }

    private Utilisateur versDomaine(UtilisateurEntity entity) {
        Set<CodeRole> codes = roles.findByUtilisateurId(entity.getId()).stream()
                .map(UtilisateurRoleEntity::getRoleCode)
                .map(CodeRole::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(CodeRole.class)));
        if (codes.isEmpty()) {
            codes = EnumSet.noneOf(CodeRole.class);
        }
        return new Utilisateur(
                entity.getId(),
                entity.getIdentifiant(),
                entity.getMotDePasseHache(),
                entity.getNomAffichage(),
                StatutCompte.valueOf(entity.getStatut()),
                entity.getEchecsConsecutifs(),
                entity.getVerrouilleJusqua(),
                entity.isDoitChangerMotDePasse(),
                codes,
                perimetres.findByUtilisateurId(entity.getId()).stream()
                        .map(UtilisateurPerimetreEntity::getComiteId)
                        .collect(Collectors.toSet()));
    }
}
