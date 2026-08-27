# Dossier de recette UAT (ISS-066)

Date d'ouverture : 2026-08-26. Résultats **automatisés** vs **à signer sur le terrain**.

| Id | Résultat | Date | Preuve |
| --- | --- | --- | --- |
| UAT-01 | OK auto (parcours signalement web) | 2026-08-26 | `SignalementApiTest` |
| UAT-02 | OK auto (corroboration 24 h) | 2026-08-26 | `CorroborationEtRg02Test` |
| UAT-03 | OK auto (SMS simulé ≤ 160, GSM-7) | 2026-08-26 | `SimulationCanalApiTest` |
| UAT-04 | OK auto (cycle intervention) | 2026-08-26 | `CycleInterventionApiTest` |
| UAT-05 | OK auto (idempotence + file locale PWA) | 2026-08-26 | `SignalementApiTest`, sync PWA |
| UAT-06 | OK auto (rétablissement + KPI) | 2026-08-26 | `CycleInterventionApiTest`, `CarteEtKpiApiTest` |
| UAT-07 | OK auto (règles R1–R5, alerte explicable) | 2026-08-26 | tests Python + `AnalyticsInterneApiTest` |
| UAT-08 | OK auto (contestation RG-07) | 2026-08-26 | `AlerteCycleDeVieTest` |
| UAT-09 | OK auto (filtre KPI + export CSV/PDF) | 2026-08-26 | `CarteEtKpiApiTest`, `FinalisationV1ApiTest` |
| UAT-10 | OK auto (403 KPI anonyme / simulation délégué) | 2026-08-26 | `SecuriteRbacEtEntetesTest` |
| UAT-11 | À signer (arrêter le service `data`, signaler encore) | | compose |
| UAT-12 | À signer (restauration dump, voir `SAUVEGARDES.md`) | | |

**Compose relu :** la procédure est `docs/guides/INSTALLATION.md` (une personne hors développement doit cocher ici).
