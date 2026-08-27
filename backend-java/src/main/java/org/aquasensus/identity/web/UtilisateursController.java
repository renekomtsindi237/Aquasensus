package org.aquasensus.identity.web;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.aquasensus.identity.application.CompteService;
import org.aquasensus.identity.domain.CodeRole;
import org.aquasensus.identity.domain.StatutCompte;
import org.aquasensus.identity.domain.Utilisateur;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UtilisateursController {

    private final CompteService comptes;

    public UtilisateursController(CompteService comptes) {
        this.comptes = comptes;
    }

    @GetMapping
    public List<CompteVue> lister() {
        return comptes.lister().stream().map(CompteVue::depuis).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompteVue creer(@RequestBody CreationCompte corps) {
        return CompteVue.depuis(comptes.creer(
                corps.identifiant(),
                corps.nomAffichage(),
                corps.motDePasseTemporaire(),
                corps.roles(),
                corps.comiteIds() == null ? Set.of() : corps.comiteIds()));
    }

    @PatchMapping("/{id}")
    public CompteVue patcher(@PathVariable UUID id, @RequestBody PatchCompte corps) {
        return CompteVue.depuis(comptes.patcher(id, corps.statut()));
    }

    public record CreationCompte(
            String identifiant,
            String nomAffichage,
            String motDePasseTemporaire,
            Set<CodeRole> roles,
            Set<UUID> comiteIds) {}

    public record PatchCompte(StatutCompte statut) {}

    public record CompteVue(
            UUID id, String identifiant, String nomAffichage, StatutCompte statut, Set<CodeRole> roles) {
        static CompteVue depuis(Utilisateur u) {
            return new CompteVue(u.id(), u.identifiant(), u.nomAffichage(), u.statut(), u.roles());
        }
    }
}
