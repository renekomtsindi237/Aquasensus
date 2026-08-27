package org.aquasensus.analytics.application;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

public final class PdfSynthese {

    private PdfSynthese() {}

    public static byte[] mensuel(
            TableauBordService.KpiAgreges k, Instant debut, Instant fin, UUID localiteId, UUID comiteId) {
        String texte = "AquaSensus synthese (EF-55). Aucun volume d'eau. "
                + "Periode "
                + debut
                + " / "
                + fin
                + " mediane="
                + k.retablissementMedianMinutes()
                + " p90="
                + k.retablissementP90Minutes()
                + " alertes="
                + k.alertesActives()
                + " filtres localite="
                + localiteId
                + " comite="
                + comiteId;
        if (texte.length() > 180) {
            texte = texte.substring(0, 180);
        }
        String escaped = texte.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
        String stream = "BT /F1 11 Tf 40 780 Td (" + escaped + ") Tj ET";
        byte[] streamBytes = stream.getBytes(StandardCharsets.ISO_8859_1);
        StringBuilder pdf = new StringBuilder();
        pdf.append("%PDF-1.4\n");
        int[] offsets = new int[6];
        offsets[1] = pdf.length();
        pdf.append("1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj\n");
        offsets[2] = pdf.length();
        pdf.append("2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj\n");
        offsets[3] = pdf.length();
        pdf.append(
                "3 0 obj<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>endobj\n");
        offsets[4] = pdf.length();
        pdf.append("4 0 obj<< /Length ")
                .append(streamBytes.length)
                .append(" >>stream\n")
                .append(stream)
                .append("\nendstream\nendobj\n");
        offsets[5] = pdf.length();
        pdf.append("5 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\n");
        int xref = pdf.length();
        pdf.append("xref\n0 6\n0000000000 65535 f \n");
        for (int i = 1; i <= 5; i++) {
            pdf.append(String.format("%010d 00000 n \n", offsets[i]));
        }
        pdf.append("trailer<< /Size 6 /Root 1 0 R >>\nstartxref\n").append(xref).append("\n%%EOF");
        return pdf.toString().getBytes(StandardCharsets.ISO_8859_1);
    }
}
