package org.aquasensus.messaging.domain;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aquasensus.reporting.domain.CategorieSymptome;

public final class AnalyseurSms {

    public static final String AIDE =
            "Envoyez: AQS CODE SYMPTOME. Ex: AQS YDE-042 PANNE. Codes: PANNE DEBIT TROUBLE ODEUR BRUIT FUITE CASSE ATTENTE AUTRE";

    private static final Pattern MOTIF = Pattern.compile(
            "^\\s*AQS\\s+(\\S+)\\s+(\\S+)(?:\\s+(.+))?$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Map<String, CategorieSymptome> CODES = Map.of(
            "PANNE", CategorieSymptome.PANNE_TOTALE,
            "DEBIT", CategorieSymptome.DEBIT_FAIBLE,
            "TROUBLE", CategorieSymptome.EAU_TROUBLE,
            "ODEUR", CategorieSymptome.EAU_MALODORANTE,
            "BRUIT", CategorieSymptome.BRUIT_ANORMAL,
            "FUITE", CategorieSymptome.FUITE,
            "CASSE", CategorieSymptome.DEGRADATION_OUVRAGE,
            "ATTENTE", CategorieSymptome.ATTENTE_EXCESSIVE,
            "AUTRE", CategorieSymptome.AUTRE);

    private AnalyseurSms() {}

    public static Optional<SmsDecode> decoder(String brut) {
        if (brut == null) {
            return Optional.empty();
        }
        String compacte = brut.trim().replaceAll("\\s+", " ");
        Matcher m = MOTIF.matcher(compacte);
        if (!m.matches()) {
            return Optional.empty();
        }
        CategorieSymptome cat = CODES.get(m.group(2).toUpperCase(Locale.ROOT));
        if (cat == null) {
            return Optional.empty();
        }
        String commentaire = m.group(3) == null ? null : m.group(3).trim();
        return Optional.of(new SmsDecode(m.group(1).toUpperCase(Locale.ROOT), cat, commentaire));
    }

    public static String gsm7(String texte) {
        String sansAccent = java.text.Normalizer.normalize(texte, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('’', '\'')
                .replace('–', '-');
        String propre = sansAccent.replaceAll("[^\\x20-\\x7E]", " ");
        return propre.length() <= 160 ? propre : propre.substring(0, 160);
    }

    public record SmsDecode(String codePointEau, CategorieSymptome categorie, String commentaire) {}
}
