# AquaSensus — Bible de contexte (source de vérité)

**Nom :** AquaSensus  
**Sous-titre :** Plateforme de suivi et de maintenance prédictive des forages communautaires  
**Territoire cible :** Quartiers périphériques de Yaoundé, zones rurales et semi-rurales du Cameroun  
**Nature :** Application multi-plateforme à impact social (génie logiciel + data + cloud)  
**Statut de ce fichier :** index canonique. Toute décision produit, métier ou technique doit rester cohérente avec ce document. En cas de conflit, **mettre à jour ce fichier** plutôt que d’improviser.

---

## 1. Identité et fibre sociale

AquaSensus n’est pas un démonstrateur IoT ni un outil ministériel. C’est une **infrastructure logicielle légère, collaborative et data-driven** pour que les communautés gardent l’eau potable **avant** que la pompe lâche.

**Oui : cette orientation utilité publique correspond à la fibre du projet.**  
Le travail vise un impact concret (santé, temps des femmes et des enfants, cohésion de quartier), sans barrière administrative lourde, et une valorisation académique et humaine (jury, ONG, mairie).

Ce qui est hors fibre :

- capteurs IoT hors de prix comme prérequis ;
- accords gouvernementaux, opérateurs télécoms ou banques comme dépendance ;
- KYC lourd ;
- solution propriétaire verrouillée.

---

## 2. Le problème social réel

Dans de nombreux quartiers, les populations dépendent de **forages communautaires** ou de **mini-réseaux** gérés par des comités de quartier.

| Symptôme | Conséquence |
| --- | --- |
| Pannes répétées par manque de maintenance préventive | Des centaines de familles sans eau potable du jour au lendemain |
| Réparation à l’aveugle, cotisations lentes, technicien qui arrive tard | Semaines de rupture |
| Corvée d’eau reportée sur femmes et enfants | Perte de temps, d’école, de revenus |
| Recours à l’eau de surface | Risques sanitaires |
| Pénurie prolongée | Tensions communautaires |

**Drame quotidien à résoudre :** passer d’une gestion réactive (la pompe est déjà morte) à une gestion **anticipée et coordonnée**.

---

## 3. Comment AquaSensus le résout

Pas besoin de capteurs chers ni d’accords étatiques. La plateforme **collecte les signalements humains** et l’**historique des pannes**, estime une **charge d’usage sans aucun comptage**, puis produit des **alertes d’usure** pour le comité de maintenance.

**Constat de terrain déterminant :** dans la grande majorité des cas, les habitants utilisent seulement l’installation hydraulique. À la fin d’une journée, **personne ne peut dire quel volume d’eau a été consommé**. Il n’y a ni compteur, ni relevé de bidons, ni estimation quotidienne possible. Demander ce chiffre, même « approximatif », produirait des écrans que personne ne remplit et des indicateurs toujours vides.

AquaSensus **ne collecte donc aucun volume**. L’usure d’usage est un proxy calculé à partir de données déjà connues : population desservie, temps écoulé depuis la dernière maintenance, saison.

### 3.1 Côté data (certification / pipeline IBM-style)

Pipeline qui agrège :

- rapports d’incidents (observation humaine) ;
- historique des pannes et des interventions par point d’eau ;
- charge d’usage estimée (référentiel + calendrier saisonnier), jamais mesurée.

Modèle **simple** (règles statistiques / séries temporelles, pas un usine à gaz ML) :

> Exemple métier : « Ce forage a atteint 93 % de son échéance d’entretien pour 450 habitants desservis, dont 40 jours de saison sèche — risque de panne sous ~2 semaines, alerter le comité. »

### 3.2 Côté génie logiciel (API et architecture)

- API REST **propre et sécurisée** (cœur métier Java / Spring ; traitements data Python).
- Signalement par habitants ou délégués : interface web **ultra-légère** (Angular PWA) et mobile (Flutter), plus **SMS/USSD simulé** (pas d’accord opérateur réel requis).
- Tableau de bord associations / mairies d’arrondissement :
  - carte de l’état de santé des points d’eau ;
  - suivi des interventions techniciens ;
  - **temps de rétablissement** (KPI social fort).

### 3.3 Côté cloud / DevOps

- Déploiement **Linux + Docker**, open-source, léger, reproductible chez une ONG ou une mairie.
- Données de signalement **simulables** ou testables avec des retours d’expérience réels (entourage), sans API secrètes.

---

## 4. Acteurs et RBAC

Pas de moteur KYC lourd. Contrôle d’accès **par rôles**.

| Rôle | Intention |
| --- | --- |
| Habitant / usager | Signaler un dysfonctionnement, consulter l’état d’un point d’eau proche |
| Délégué / comité de quartier | Valider, prioriser, déclencher la maintenance, voir les alertes prédictives |
| Technicien | Recevoir l’intervention, consigner diagnostic et clôture |
| Association locale / mairie d’arrondissement | Carte, KPI, suivi global, temps de rétablissement |
| Administrateur technique | Utilisateurs, référentiel des forages, paramétrage |

---

## 5. Périmètre produit (v1 vs hors v1)

### Dans le périmètre

- Référentiel des points d’eau (forages, mini-réseaux) : localisation, comité, historique.
- Signalement d’incident / dysfonctionnement.
- Cycle d’intervention (ouvert → en cours → rétabli) et mesure du délai.
- Charge d’usage **estimée** (population, calendrier, dernière maintenance) — sans aucune saisie de volume.
- Alertes de seuil critique (règles + séries temporelles simples).
- Carte + tableau de bord KPI.
- Canal SMS/USSD **simulé**.
- Auth RBAC, API documentée, Docker.

### Hors périmètre (sauf décision explicite et mise à jour de ce fichier)

- Dépendance à des capteurs IoT payants.
- Comptage des volumes tirés (compteur, relevé de bidons, estimation quotidienne) : inconnaissable sur le terrain.
- Contrats opérateurs USSD/SMS réels.
- Accords ministériels comme prérequis.
- Lakehouse « big data » comme condition de la v1 (la couche lakehouse est une **évolution** prévue, pas un bloqueur du signalement).
- Modèles ML complexes, deep learning, IoT temps réel.

---

## 6. KPI sociaux (ce qu’on mesure vraiment)

1. **Temps de rétablissement** après signalement (médiane, P90).
2. **Taux de pannes évitées / alertes anticipées** (alertes émises avant panne déclarée).
3. **Couverture** : points d’eau suivis vs quartier.
4. **Délai comité → technicien** (réactivité organisationnelle).
5. **Continuité d’accès** : jours sans eau évités (estimation à partir des historiques).

Un écran ou un endpoint qui n’aide pas ces KPI doit être justifié.

---

## 7. Stack technique retenue (implémentation)

Le pitch peut citer FastAPI comme *idée* d’API légère. **L’implémentation du dépôt est :**

| Couche | Choix |
| --- | --- |
| Frontend web | Angular (SPA + PWA installable), interface légère |
| Mobile / PWA | Flutter |
| Cœur métier, sécu, API | Java, MVC, Spring Security, REST |
| Data, prédiction, analytics | Python (pipelines, règles / séries temporelles) |
| Base relationnelle | PostgreSQL |
| Migrations | **Flyway uniquement**, scripts SQL versionnés, exécutés au démarrage Java |
| Lakehouse (évolution) | Iceberg / Delta sur object storage — logs, rapports, flux semi-structurés |
| Exécution | Linux, Docker, reproductible ONG/mairie |

**Séparation des responsabilités :**

- Java : transactions métier, utilisateurs, sécurité, exposition REST vers les fronts.
- Python : ETL, analytics, interactions lakehouse, modèles prédictifs simples.
- Angular / Flutter : consommation des API REST uniquement (pas de logique métier dupliquée).

---

## 8. Qualité et conventions

- TDD : toute feature s’accompagne de tests (JUnit/Mockito + `@SpringBootTest` ; Pytest + TestClient ; tests Angular ; widget/unit Flutter).
- Migrations Flyway propres, sans casser les contraintes existantes.
- Typage strict, modules clairs, APIs documentées (contrat inter-services).
- Prédiction : **interprétable** (règle, seuil, fenêtre temporelle) — un comité doit pouvoir comprendre l’alerte.

---

## 9. Identité visuelle

**Logo par défaut de toutes les applications du projet : `docs/design/aquasensus-logo.png`.**

| Cas | Fichier |
| --- | --- |
| Cas général (Angular, Flutter, écran de connexion, e-mails, rapports, diaporamas) | `aquasensus-logo.png` |
| Fond sombre, écran de démarrage | `aquasensus-logo-inverse.png` |
| Surface carrée ou largeur < 120 px (favicon, icône d’application, marqueur, avatar) | `aquasensus-mark-*.png` |
| Impression grand format | `aquasensus-logo.svg` |

Aucun autre fichier ne représente la marque. Les fronts copient l’actif depuis `docs/design/` sans le modifier ; les rasters se régénèrent par `python docs/design/render-brand.py`. Le détail des règles d’usage est dans `docs/CHARTE-GRAPHIQUE.md` (§3.2).

---

## 10. Vocabulaire à conserver

| Terme | Sens |
| --- | --- |
| Point d’eau / forage | Infrastructure communautaire suivie |
| Comité | Gestion locale (quartier) |
| Signalement | Rapport humain d’incident ou de dysfonctionnement |
| Intervention | Action technicien jusqu’au rétablissement |
| Seuil critique | Usage / historique qui déclenche une alerte prédictive |
| Temps de rétablissement | KPI social principal |
| SMS/USSD simulé | Canal alternatif sans opérateur réel |

---

## 11. Pourquoi le projet « coche » (rappel jury / partenaires)

1. **Impact social direct** : moins de pannes surprises, réparations mieux ciblées.
2. **Peu de barrières** : pas d’API bancaire ni d’opérateur ; données simulables ou de terrain proche.
3. **Valorisation** : santé publique + accès à l’eau + ingénierie data et logiciel.

---

## 12. Index de maintenance de ce fichier

Mettre à jour cette bible quand change :

- un rôle, un KPI, le périmètre v1 ;
- la stack (Java / Python / fronts / Docker) ;
- la politique IoT, SMS réel, ML, ou le refus de comptage des volumes ;
- le logo par défaut ou un actif d’identité.

Fichiers liés :

- `planification/` — backlog d'implémentation : lots L0–L5, 68 issues regroupées par modules M1–M11 ;
- `docs/CAHIER-DES-CHARGES.md` — exigences fonctionnelles et non fonctionnelles, modèle de données, API, moteur prédictif, lotissement, recette (AQS-CDC-001) ;
- `docs/CAHIER-ANALYSE.md` — acteurs, cas d’utilisation, modèle du domaine, dynamique, invariants, traçabilité ; sans aucune technologie (AQS-ANA-001) ;
- `docs/CAHIER-CONCEPTION.md` — décisions d’architecture, couches, persistance, API, sécurité, moteur prédictif, fronts, déploiement, tests (AQS-CNC-001) ;
- `docs/diagrammes/` — modélisation UML PlantUML : 4 vues globales puis 35 vues détaillées, rendus SVG générés (AQS-UML-001) ;
- `docs/CHARTE-GRAPHIQUE.md` — identité, design system, couleurs d’état, accessibilité, ton éditorial (AQS-CHG-001) ;
- `docs/design/` — actifs opérationnels : `aquasensus-logo.png` (**logo par défaut**), variantes, sources SVG, `tokens.css`, `tokens.dart` ;
- `.cursor/rules/aquasensus-contexte.mdc` — injection automatique du contexte en session ;
- `.cursorrules.txt` — stack et TDD (raccourci historique).

Hiérarchie documentaire : cette bible prévaut sur le cahier des charges, qui prévaut sur le cahier d’analyse, puis sur le cahier de conception et les diagrammes, puis sur la charte graphique, puis sur le code. Une contradiction se résout en mettant à jour le document, jamais en improvisant un récit parallèle.

Chaîne de production : besoin social → exigences (**quoi**) → analyse (**quoi, structuré**, sans technologie) → conception (**comment**) → code. L’analyse ne nomme aucune technologie ; la conception ne crée aucune règle métier.
