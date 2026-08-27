package org.aquasensus.reporting.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.aquasensus.shared.error.RegleMetierException;

public final class TelephoneDeclarant {

    private TelephoneDeclarant() {}

    public static String normaliser(String brut) {
        if (brut == null) {
            return null;
        }
        String digits = brut.replaceAll("\\D", "");
        if (digits.length() < 8) {
            throw new RegleMetierException("EF-11", "Numéro de téléphone incomplet.");
        }
        return digits;
    }

    public static String hacher(String normalise) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(normalise.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String suffixe(String normalise) {
        return normalise.substring(normalise.length() - 4);
    }
}
