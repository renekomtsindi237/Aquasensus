package org.aquasensus.identity.web;

import java.util.Set;
import org.aquasensus.identity.application.JetonAuthentification;
import org.aquasensus.identity.domain.CodeRole;

public record AuthReponse(
        String jetonAcces,
        String jetonRafraichissement,
        String nomAffichage,
        Set<CodeRole> roles,
        boolean doitChangerMotDePasse) {

    public static AuthReponse depuis(JetonAuthentification jeton) {
        return new AuthReponse(
                jeton.jetonAcces(),
                jeton.jetonRafraichissement(),
                jeton.nomAffichage(),
                jeton.roles(),
                jeton.doitChangerMotDePasse());
    }
}
