package org.aquasensus.registry.domain;

import java.util.List;
import java.util.UUID;

public record Localite(UUID id, String code, String nom, NiveauLocalite niveau, UUID parentId) {

    public String cheminAvec(List<Localite> ancetresDuParentVersRacine) {
        StringBuilder sb = new StringBuilder(nom);
        for (Localite a : ancetresDuParentVersRacine) {
            sb.insert(0, a.nom() + " / ");
        }
        return sb.toString();
    }
}
