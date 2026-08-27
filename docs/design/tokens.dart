// ---------------------------------------------------------------------------
// AquaSensus - Tokens de design (Flutter)
// Reference : docs/CHARTE-GRAPHIQUE.md (AQS-CHG-001 v1.0)
//
// Regle : aucune valeur Color(0x...) ni taille brute hors de ce fichier.
// Les ecrans consomment AqsColors / AqsSpacing / AqsTypography ou le Theme.
// ---------------------------------------------------------------------------

import 'dart:ui' show FontFeature;

import 'package:flutter/material.dart';

/// Palette de la charte. Les etats sont reserves : jamais d'usage decoratif.
abstract final class AqsColors {
  // Bleu Nappe
  static const blue50 = Color(0xFFEFF8FC);
  static const blue100 = Color(0xFFD6EDF7);
  static const blue200 = Color(0xFFADDAEF);
  static const blue300 = Color(0xFF7BC2E2);
  static const blue400 = Color(0xFF45A5D0);
  static const blue500 = Color(0xFF1B87B8);
  static const blue600 = Color(0xFF106D99);
  static const blue700 = Color(0xFF0C577C);
  static const blue800 = Color(0xFF0B4763);
  static const blue900 = Color(0xFF0A3A52);
  static const blue950 = Color(0xFF06283A);

  // Ocre Laterite
  static const earth100 = Color(0xFFF7ECE2);
  static const earth300 = Color(0xFFE2B78E);
  static const earth500 = Color(0xFFC86A2C);
  static const earth700 = Color(0xFFA2521F);
  static const earth900 = Color(0xFF6B3512);

  // Neutres
  static const neutral0 = Color(0xFFFFFFFF);
  static const neutral50 = Color(0xFFF7F9FB);
  static const neutral100 = Color(0xFFEEF2F6);
  static const neutral200 = Color(0xFFDDE4EB);
  static const neutral300 = Color(0xFFC3CDD8);
  static const neutral400 = Color(0xFF94A3B3);
  static const neutral500 = Color(0xFF6B7B8C);
  static const neutral600 = Color(0xFF51606F);
  static const neutral700 = Color(0xFF3B4855);
  static const neutral800 = Color(0xFF27313B);
  static const neutral900 = Color(0xFF161E26);

  // Semantiques - action et marque
  static const action = blue600;
  static const actionHover = blue700;
  static const brand = blue500;
  static const brandDeep = blue800;
  static const focusRing = blue400;
  static const chrome = blue900;
  static const chromeHover = blue800;
  static const chromeActive = blue700;
  static const chromeText = neutral0;
  static const chromeMuted = blue200;

  // Semantiques - etats des points d'eau (mode clair)
  static const stateOperationnel = Color(0xFF1E8E4E);
  static const stateOperationnelBg = Color(0xFFE6F4EC);
  static const stateOperationnelText = Color(0xFF16713E);

  static const stateSurveillance = Color(0xFFF2A900);
  static const stateSurveillanceBg = Color(0xFFFEF3DC);
  static const stateSurveillanceText = Color(0xFF8A6000);

  static const stateRisque = Color(0xFFD2620E);
  static const stateRisqueBg = Color(0xFFFDEEE1);
  static const stateRisqueText = Color(0xFFA34A08);

  static const statePanne = Color(0xFFC62828);
  static const statePanneBg = Color(0xFFFCEAEA);
  static const statePanneText = Color(0xFFA31D1D);

  static const stateReparation = blue600;
  static const stateReparationBg = blue100;
  static const stateReparationText = blue800;

  static const stateHorsService = Color(0xFF64748B);
  static const stateHorsServiceBg = neutral100;
  static const stateHorsServiceText = neutral700;

  static const stateInconnu = neutral400;

  // Semantiques - etats des points d'eau (mode sombre)
  static const stateOperationnelDark = Color(0xFF4CC47E);
  static const stateSurveillanceDark = Color(0xFFFFC94D);
  static const stateRisqueDark = Color(0xFFFF9B52);
  static const statePanneDark = Color(0xFFFF7373);

  // Semantiques - retroaction systeme
  static const feedbackSuccess = stateOperationnel;
  static const feedbackInfo = action;
  static const feedbackWarning = stateSurveillance;
  static const feedbackError = statePanne;
  static const feedbackOffline = Color(0xFF64748B);
}

/// Etats fonctionnels d'un point d'eau, avec leur triple codage
/// couleur + forme + libelle (exigence ENF-43 : jamais la couleur seule).
enum AqsWaterPointState {
  operationnel(
    'Operationnel',
    AqsColors.stateOperationnel,
    AqsColors.stateOperationnelBg,
    AqsColors.stateOperationnelText,
    AqsStateShape.circle,
    Icons.water_drop_outlined,
  ),
  sousSurveillance(
    'Sous surveillance',
    AqsColors.stateSurveillance,
    AqsColors.stateSurveillanceBg,
    AqsColors.stateSurveillanceText,
    AqsStateShape.ring,
    Icons.visibility_outlined,
  ),
  risqueEleve(
    'Risque eleve',
    AqsColors.stateRisque,
    AqsColors.stateRisqueBg,
    AqsColors.stateRisqueText,
    AqsStateShape.triangle,
    Icons.warning_amber_outlined,
  ),
  enPanne(
    'En panne',
    AqsColors.statePanne,
    AqsColors.statePanneBg,
    AqsColors.statePanneText,
    AqsStateShape.diamond,
    Icons.close_rounded,
  ),
  enReparation(
    'En reparation',
    AqsColors.stateReparation,
    AqsColors.stateReparationBg,
    AqsColors.stateReparationText,
    AqsStateShape.diamondOutlined,
    Icons.build_outlined,
  ),
  horsService(
    'Hors service',
    AqsColors.stateHorsService,
    AqsColors.stateHorsServiceBg,
    AqsColors.stateHorsServiceText,
    AqsStateShape.circleBarred,
    Icons.lock_outline,
  );

  const AqsWaterPointState(
    this.label,
    this.color,
    this.background,
    this.onBackground,
    this.shape,
    this.icon,
  );

  final String label;
  final Color color;
  final Color background;
  final Color onBackground;
  final AqsStateShape shape;
  final IconData icon;
}

/// Silhouette du marqueur : garantit la lisibilite en niveaux de gris.
enum AqsStateShape { circle, ring, triangle, diamond, diamondOutlined, circleBarred }

abstract final class AqsSpacing {
  static const s1 = 4.0;
  static const s2 = 8.0;
  static const s3 = 12.0;
  static const s4 = 16.0;
  static const s5 = 20.0;
  static const s6 = 24.0;
  static const s8 = 32.0;
  static const s10 = 40.0;
  static const s12 = 48.0;
  static const s16 = 64.0;
}

abstract final class AqsRadius {
  static const sm = 4.0;
  static const md = 8.0;
  static const lg = 12.0;
  static const xl = 16.0;
  static const pill = 999.0;
}

/// Hauteurs minimales de cible tactile (ENF-42).
abstract final class AqsTargets {
  static const min = 48.0;
  static const field = 56.0;
}

abstract final class AqsElevation {
  static const level1 = [BoxShadow(color: Color(0x140B4763), blurRadius: 2, offset: Offset(0, 1))];
  static const level2 = [BoxShadow(color: Color(0x1A0B4763), blurRadius: 8, offset: Offset(0, 2))];
  static const level3 = [BoxShadow(color: Color(0x240B4763), blurRadius: 24, offset: Offset(0, 8))];
  static const level4 = [BoxShadow(color: Color(0x2E0B4763), blurRadius: 40, offset: Offset(0, 16))];
}

abstract final class AqsMotion {
  static const fast = Duration(milliseconds: 120);
  static const base = Duration(milliseconds: 200);
  static const slow = Duration(milliseconds: 320);
  static const curve = Curves.easeInOutCubic;
}

abstract final class AqsTypography {
  static const sans = 'Inter';
  static const mono = 'IBMPlexMono';

  static const display = TextStyle(
      fontFamily: sans, fontSize: 40, height: 1.2, fontWeight: FontWeight.w700, letterSpacing: -0.8);
  static const h1 = TextStyle(
      fontFamily: sans, fontSize: 32, height: 1.25, fontWeight: FontWeight.w700, letterSpacing: -0.32);
  static const h2 = TextStyle(
      fontFamily: sans, fontSize: 24, height: 1.33, fontWeight: FontWeight.w600, letterSpacing: -0.24);
  static const h3 =
      TextStyle(fontFamily: sans, fontSize: 20, height: 1.4, fontWeight: FontWeight.w600);
  static const h4 =
      TextStyle(fontFamily: sans, fontSize: 18, height: 1.44, fontWeight: FontWeight.w600);
  static const bodyLarge =
      TextStyle(fontFamily: sans, fontSize: 17, height: 1.53, fontWeight: FontWeight.w400);
  static const body =
      TextStyle(fontFamily: sans, fontSize: 16, height: 1.5, fontWeight: FontWeight.w400);
  static const bodySmall =
      TextStyle(fontFamily: sans, fontSize: 14, height: 1.5, fontWeight: FontWeight.w400);
  static const label = TextStyle(
      fontFamily: sans, fontSize: 14, height: 1.43, fontWeight: FontWeight.w500, letterSpacing: 0.14);
  static const caption = TextStyle(
      fontFamily: sans, fontSize: 12, height: 1.33, fontWeight: FontWeight.w400, letterSpacing: 0.12);
  static const overline = TextStyle(
      fontFamily: sans, fontSize: 11, height: 1.45, fontWeight: FontWeight.w600, letterSpacing: 0.88);

  /// Valeurs chiffrees : chiffres tabulaires pour l'alignement en colonne.
  static const metricXl = TextStyle(
    fontFamily: mono,
    fontSize: 36,
    height: 1.11,
    fontWeight: FontWeight.w600,
    fontFeatures: [FontFeature.tabularFigures()],
  );
  static const metric = TextStyle(
    fontFamily: mono,
    fontSize: 24,
    height: 1.17,
    fontWeight: FontWeight.w600,
    fontFeatures: [FontFeature.tabularFigures()],
  );
  static const code = TextStyle(
      fontFamily: mono, fontSize: 14, height: 1.43, fontWeight: FontWeight.w500);
}

/// Themes clair et sombre derives des tokens.
abstract final class AqsTheme {
  static ThemeData light() => _build(
        brightness: Brightness.light,
        scheme: const ColorScheme.light(
          primary: AqsColors.action,
          onPrimary: AqsColors.neutral0,
          secondary: AqsColors.earth500,
          onSecondary: AqsColors.neutral0,
          error: AqsColors.statePanne,
          onError: AqsColors.neutral0,
          surface: AqsColors.neutral0,
          onSurface: AqsColors.neutral900,
        ),
        background: AqsColors.neutral50,
        border: AqsColors.neutral300,
        secondaryText: AqsColors.neutral600,
      );

  static ThemeData dark() => _build(
        brightness: Brightness.dark,
        scheme: const ColorScheme.dark(
          primary: AqsColors.blue400,
          onPrimary: AqsColors.blue950,
          secondary: AqsColors.earth300,
          onSecondary: AqsColors.blue950,
          error: AqsColors.statePanneDark,
          onError: AqsColors.blue950,
          surface: Color(0xFF0F2B3C),
          onSurface: Color(0xFFEAF2F7),
        ),
        background: AqsColors.blue950,
        border: const Color(0xFF20465C),
        secondaryText: const Color(0xFFA7BDCB),
      );

  static ThemeData _build({
    required Brightness brightness,
    required ColorScheme scheme,
    required Color background,
    required Color border,
    required Color secondaryText,
  }) {
    return ThemeData(
      useMaterial3: true,
      brightness: brightness,
      colorScheme: scheme,
      scaffoldBackgroundColor: background,
      fontFamily: AqsTypography.sans,
      textTheme: TextTheme(
        displayLarge: AqsTypography.display.copyWith(color: scheme.onSurface),
        headlineLarge: AqsTypography.h1.copyWith(color: scheme.onSurface),
        headlineMedium: AqsTypography.h2.copyWith(color: scheme.onSurface),
        titleLarge: AqsTypography.h3.copyWith(color: scheme.onSurface),
        titleMedium: AqsTypography.h4.copyWith(color: scheme.onSurface),
        bodyLarge: AqsTypography.bodyLarge.copyWith(color: scheme.onSurface),
        bodyMedium: AqsTypography.body.copyWith(color: scheme.onSurface),
        bodySmall: AqsTypography.bodySmall.copyWith(color: secondaryText),
        labelLarge: AqsTypography.label.copyWith(color: scheme.onSurface),
        labelSmall: AqsTypography.caption.copyWith(color: secondaryText),
      ),
      filledButtonTheme: FilledButtonThemeData(
        style: FilledButton.styleFrom(
          minimumSize: const Size(64, AqsTargets.min),
          padding: const EdgeInsets.symmetric(horizontal: AqsSpacing.s5),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(AqsRadius.md)),
          textStyle: AqsTypography.body.copyWith(fontWeight: FontWeight.w600),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          minimumSize: const Size(64, AqsTargets.min),
          side: BorderSide(color: scheme.primary),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(AqsRadius.md)),
          textStyle: AqsTypography.body.copyWith(fontWeight: FontWeight.w600),
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: scheme.surface,
        contentPadding: const EdgeInsets.symmetric(
            horizontal: AqsSpacing.s4, vertical: AqsSpacing.s3),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(AqsRadius.md),
          borderSide: BorderSide(color: border),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(AqsRadius.md),
          borderSide: BorderSide(color: border),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(AqsRadius.md),
          borderSide: BorderSide(color: scheme.primary, width: 2),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(AqsRadius.md),
          borderSide: BorderSide(color: scheme.error, width: 2),
        ),
        labelStyle: AqsTypography.label.copyWith(color: secondaryText),
        helperStyle: AqsTypography.caption.copyWith(color: secondaryText),
      ),
      cardTheme: CardThemeData(
        color: scheme.surface,
        elevation: brightness == Brightness.light ? 1 : 0,
        margin: EdgeInsets.zero,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AqsRadius.lg),
          side: BorderSide(color: border),
        ),
      ),
      chipTheme: ChipThemeData(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(AqsRadius.pill)),
        labelStyle: AqsTypography.bodySmall.copyWith(fontWeight: FontWeight.w600),
        padding: const EdgeInsets.symmetric(horizontal: AqsSpacing.s2, vertical: AqsSpacing.s1),
      ),
      dividerTheme: DividerThemeData(color: border, thickness: 1, space: 1),
      snackBarTheme: SnackBarThemeData(
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(AqsRadius.md)),
      ),
    );
  }
}
