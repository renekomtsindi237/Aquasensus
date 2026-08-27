# Matrice RG → tests (ISS-068, CDC §17.3)

| Règle | Test automatisé |
| --- | --- |
| RG-01 | Création point d'eau liée à un comité (`PointEauApiTest`) |
| RG-02 | `CorroborationEtRg02Test` |
| RG-03 | Fenêtre 24 h, même catégorie (`CorroborationEtRg02Test`) |
| RG-04 | Temps de rétablissement à la confirmation (`CycleInterventionApiTest`, `MachineEtatsInterventionTest`) |
| RG-05 | Compte rendu incomplet refusé (`MachineEtatsInterventionTest`) |
| RG-06 | Taux d'anticipation KPI (`TableauBordService` / `CarteEtKpiApiTest`) |
| RG-07 | `AlerteCycleDeVieTest` |
| RG-08 / RG-16 | Confiance et rétention d'alertes charge (`data-python/tests`) |
| RG-09 | Périmètre délégué (`AuthEtPerimetreTest`) |
| RG-10 | Hash téléphone journal SMS (`SimulationCanalApiTest`, table `numero_hache`) |
| RG-11 | Rejet sans changement d'état (domaine signalement) |
| RG-12 | `CarteEtKpiApiTest` (note RG-12, hors dispo) |
| RG-13 | Désactivation logique points (`PointEauService` / API) |
| RG-14 | Priorité figée (qualification) |
| RG-15 | Pipeline Python quotidien (`test_evaluation.py`) |

Couverture ligne Java : JaCoCo, seuil 55 % du bundle (CI rouge en dessous). Cœur Python : `pytest --cov=aquasensus_data --cov-fail-under=70`.
