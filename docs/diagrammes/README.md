# AquaSensus — Modélisation UML (PlantUML)

| Métadonnée | Valeur |
| --- | --- |
| Référence | AQS-UML-001 |
| Version | 1.0 |
| Outil | PlantUML (sources `.puml` versionnées, rendus SVG générés) |
| Documents liés | `docs/CAHIER-ANALYSE.md`, `docs/CAHIER-CONCEPTION.md`, `docs/CAHIER-DES-CHARGES.md`, `docs/CHARTE-GRAPHIQUE.md` |

Les sources `.puml` font foi. Les fichiers de `rendus/` sont générés et ne se modifient jamais à la main.

---

## Principe d'organisation

La modélisation va du général au particulier, conformément à la demande de conception :

1. **`00-global/`** — quatre vues d'ensemble, une par type de diagramme. Elles donnent la carte du système sans détail.
2. **`01-` à `05-`** — déclinaison élément par élément : un diagramme par module, par domaine ou par processus.

Un diagramme global ne cherche pas l'exhaustivité : il cherche la lisibilité. Les inclusions, extensions, invariants et cas limites vivent dans les diagrammes détaillés.

---

## Index des diagrammes

### 00 — Vues globales

| Fichier | Type | Objet |
| --- | --- | --- |
| `G1-cas-utilisation-global.puml` | Cas d'utilisation | Onze intentions métier et leurs acteurs, y compris les acteurs système |
| `G2-classes-domaine-global.puml` | Classes | Modèle du domaine complet, sans détail technique |
| `G3-activite-processus-global.puml` | Activité | Processus complet, du symptôme constaté au rétablissement confirmé |
| `G4-sequence-bout-en-bout.puml` | Séquence | Trajet technique d'un signalement jusqu'au calcul du KPI |

### 01 — Cas d'utilisation par module

| Fichier | Module | Exigences couvertes |
| --- | --- | --- |
| `UC1-referentiel.puml` | M1 Référentiel | EF-01 à EF-07 |
| `UC2-signalement.puml` | M2 Signalement | EF-10 à EF-17 |
| `UC3-maintenance.puml` | M3 Interventions | EF-20 à EF-28 |
| `UC4-charge-saisonnalite.puml` | M4 Charge d'usage | EF-30 à EF-35 |
| `UC5-prediction-alertes.puml` | M5 Prédiction | EF-40 à EF-47 |
| `UC6-pilotage-kpi.puml` | M6 Restitution | EF-50 à EF-56 |
| `UC7-canal-sms-ussd.puml` | M7 Canal simulé | EF-60 à EF-66 |
| `UC8-administration-identite.puml` | M9 et M10 | EF-80 à EF-85, EF-90 à EF-92 |

### 02 — Classes par domaine

| Fichier | Domaine | Contenu |
| --- | --- | --- |
| `CL1-identite-rbac.puml` | `identity` | Utilisateur, rôles, permissions, périmètre, téléphone haché |
| `CL2-referentiel.puml` | `registry` | Localité, comité, point d'eau, historique d'état, coordonnées |
| `CL3-signalement.puml` | `reporting` | Signalement, corroboration, déclarant, politique de priorité |
| `CL4-maintenance.puml` | `maintenance` | Intervention, compte rendu, jalons, pièces, durée de rétablissement |
| `CL5-charge-usage.puml` | `charge` | Charge estimée en jours pondérés, calendrier saisonnier, intervalle de maintenance effectif |
| `CL6-prediction.puml` | `prediction` | Indice de santé, facteurs, alerte, règles R1 à R5, paramétrage |
| `CL7-messagerie-simulee.puml` | `messaging` | Port `MessagingGateway`, simulateur, session USSD, notifications |
| `CL8-conception-couches.puml` | Conception | Découpage présentation / application / domaine / infrastructure |

Les sept premiers sont des vues **d'analyse** (langage métier). Le huitième est une vue **de conception** (ports, adaptateurs, technologies).

### 03 — Activités par processus

| Fichier | Processus |
| --- | --- |
| `AC1-signaler-incident.puml` | Création d'un signalement, corroboration, bascule d'état |
| `AC2-qualifier-signalement.puml` | Décision du comité : qualifier, rejeter, marquer en doublon |
| `AC3-cycle-intervention.puml` | Cycle complet de l'intervention, suspension comprise |
| `AC4-calcul-indice-alerte.puml` | Traitement quotidien : indicateurs, indice, règles, alertes |
| `AC5-traiter-alerte.puml` | Traitement d'une alerte par le comité, jusqu'à l'issue mesurée |
| `AC6-calcul-charge-usage.puml` | Calcul de la charge d'usage sans aucune mesure : date de référence, pondération saisonnière, intervalle effectif |
| `AC7-synchronisation-hors-ligne.puml` | File locale, idempotence, conflits |

### 04 — Séquences

| Fichier | Scénario |
| --- | --- |
| `SQ1-authentification.puml` | Connexion, verrouillage, autorisation, mot de passe oublié |
| `SQ2-signalement-pwa.puml` | Signalement web, corroboration, rejeu idempotent |
| `SQ3-signalement-sms.puml` | SMS entrant, analyse, accusé de réception |
| `SQ4-session-ussd.puml` | Arborescence USSD à état, expiration de session |
| `SQ5-qualification-affectation.puml` | Qualification, affectation, dossier de préparation |
| `SQ6-intervention-hors-ligne.puml` | Intervention terrain sans réseau, resynchronisation, conflits |
| `SQ7-cloture-et-kpi.puml` | Confirmation du rétablissement et calcul du temps de rétablissement |
| `SQ8-pipeline-prediction.puml` | Traitement quotidien Python et évaluation a posteriori |

### 05 — Machines à états

| Fichier | Objet métier |
| --- | --- |
| `ET1-point-eau.puml` | Six états d'un ouvrage et leurs transitions |
| `ET2-signalement.puml` | Cycle de vie d'un signalement, corroboration comprise |
| `ET3-intervention.puml` | Cycle de vie d'une intervention, réouverture comprise |
| `ET4-alerte.puml` | Cycle de vie d'une alerte et évaluation de son issue |

---

## Conventions

| Règle | Détail |
| --- | --- |
| Langue | Français, vocabulaire du glossaire du cahier des charges |
| Traçabilité | Chaque diagramme cite les exigences (`EF-nn`, `ENF-nn`) et règles de gestion (`RG-nn`) qu'il modélise |
| Style | `!include ../_style.puml` en première instruction, jamais de couleur définie localement hors états métier |
| Couleurs d'état | Celles de `docs/CHARTE-GRAPHIQUE.md` §4.3 ; toujours accompagnées d'un libellé |
| Analyse vs conception | L'analyse ignore les technologies ; la conception les nomme explicitement |
| Nommage | `<Préfixe><n>-<sujet>.puml`, préfixes `G`, `UC`, `CL`, `AC`, `SQ`, `ET` |

### Pièges rencontrés (à ne pas réintroduire)

- Les variables de style (`!$aqs_*`) ne sont pas interprétées dans les déclarations `box` d'un diagramme de séquence ni dans les en-têtes de couloir : y écrire la valeur hexadécimale.
- Dans un diagramme d'activité, une branche `else` **vide** combinée à une activité colorée casse l'analyse du bloc englobant : toujours renseigner les deux branches.
- Après un `endif` qui a changé de couloir, réaffirmer le couloir voulu, sinon les activités suivantes se placent dans le mauvais.

---

## Régénérer les rendus

Les rendus SVG de `rendus/` sont produits par PlantUML. Ils ne sont pas modifiables à la main.

```bash
# Une fois : récupérer plantuml.jar (https://plantuml.com/download), Java 17+ requis
java -jar plantuml.jar -tsvg -o "../rendus/00-global"          docs/diagrammes/00-global/*.puml
java -jar plantuml.jar -tsvg -o "../rendus/01-cas-utilisation" docs/diagrammes/01-cas-utilisation/*.puml
java -jar plantuml.jar -tsvg -o "../rendus/02-classes"         docs/diagrammes/02-classes/*.puml
java -jar plantuml.jar -tsvg -o "../rendus/03-activites"       docs/diagrammes/03-activites/*.puml
java -jar plantuml.jar -tsvg -o "../rendus/04-sequences"       docs/diagrammes/04-sequences/*.puml
java -jar plantuml.jar -tsvg -o "../rendus/05-etats"           docs/diagrammes/05-etats/*.puml
```

Contrôle de syntaxe seul, sans production d'image (utile en intégration continue) :

```bash
java -jar plantuml.jar -checkonly docs/diagrammes/**/*.puml
```

En édition, l'extension PlantUML de l'IDE affiche l'aperçu en direct ; le rendu SVG n'est régénéré qu'avant livraison ou revue.

---

## Règle de mise à jour

Un diagramme qui contredit le cahier des charges est un défaut. Lorsqu'une exigence, une règle de gestion ou une machine à états évolue :

1. mettre à jour `docs/CAHIER-DES-CHARGES.md` (source des exigences) ;
2. mettre à jour le ou les `.puml` concernés, y compris les références `EF-nn` / `RG-nn` citées ;
3. régénérer les rendus ;
4. vérifier que les vues globales restent cohérentes avec les vues détaillées.
