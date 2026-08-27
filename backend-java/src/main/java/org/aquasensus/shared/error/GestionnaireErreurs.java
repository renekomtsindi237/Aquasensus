package org.aquasensus.shared.error;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GestionnaireErreurs {

    @ExceptionHandler(RessourceIntrouvableException.class)
    ProblemDetail introuvable(RessourceIntrouvableException ex) {
        return probleme(HttpStatus.NOT_FOUND, "introuvable", ex.getMessage());
    }

    @ExceptionHandler(IdentifiantsInvalidesException.class)
    ProblemDetail identifiants(IdentifiantsInvalidesException ex) {
        return probleme(HttpStatus.UNAUTHORIZED, "identifiants-invalides", ex.getMessage());
    }

    @ExceptionHandler(CompteVerrouilleException.class)
    ProblemDetail verrouille(CompteVerrouilleException ex) {
        return probleme(HttpStatus.LOCKED, "compte-verrouille", ex.getMessage());
    }

    @ExceptionHandler(AccesRefuseException.class)
    ProblemDetail refuse(AccesRefuseException ex) {
        return probleme(HttpStatus.FORBIDDEN, "acces-refuse", ex.getMessage());
    }

    @ExceptionHandler(RegleMetierException.class)
    ProblemDetail metier(RegleMetierException ex) {
        ProblemDetail pd = probleme(HttpStatus.UNPROCESSABLE_ENTITY, "regle-metier", ex.getMessage());
        pd.setProperty("codeRegle", ex.codeRegle());
        return pd;
    }

    @ExceptionHandler(ConflitException.class)
    ProblemDetail conflit(ConflitException ex) {
        return probleme(HttpStatus.CONFLICT, "conflit", ex.getMessage());
    }

    @ExceptionHandler(QuotaDepasseException.class)
    ProblemDetail quota(QuotaDepasseException ex) {
        return probleme(HttpStatus.TOO_MANY_REQUESTS, "quota", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException ex) {
        return probleme(HttpStatus.BAD_REQUEST, "requete-invalide", "Requête invalide.");
    }

    private static ProblemDetail probleme(HttpStatus statut, String type, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(statut, detail);
        pd.setType(URI.create("https://aquasensus.local/erreurs/" + type));
        pd.setTitle(statut.getReasonPhrase());
        return pd;
    }
}
