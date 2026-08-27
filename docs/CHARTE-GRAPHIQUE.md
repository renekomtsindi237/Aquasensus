# AquaSensus — Charte graphique et système de design

| Métadonnée | Valeur |
| --- | --- |
| Référence | AQS-CHG-001 |
| Version | 1.2 |
| Statut | Validé — applicable à Angular PWA, Flutter, supports imprimés et de présentation |
| Portée | Identité de marque, design system, accessibilité, ton éditorial, déclinaisons |
| Document maître | `docs/CONTEXTE-AQUASENSUS.md` (bible) |
| Documents liés | `docs/CAHIER-DES-CHARGES.md` (ENF-40 à ENF-46), `docs/design/` (actifs) |
| Actifs opérationnels | `docs/design/` : `aquasensus-logo.png` (**logo par défaut de toutes les applications**), variantes et sources SVG, rasters du symbole, `tokens.css`, `tokens.dart` |

### Historique des révisions

| Version | Date | Nature |
| --- | --- | --- |
| 1.0 | 2026-08-26 | Création : identité, palette, typographie, grille, composants, cartographie, data-viz, accessibilité, tokens, déclinaisons |
| 1.1 | 2026-08-26 | `aquasensus-logo.png` désigné logo par défaut de toutes les applications (§3.2.1) ; variantes rasterisées et script `render-brand.py` |
| 1.2 | 2026-08-26 | Suppression de toute représentation de « volume relevé » : H-2 interdit le comptage ; jauge et graphiques d'usage portent désormais sur la charge estimée |

---

## Sommaire

1. [Philosophie de design](#1-philosophie-de-design)
2. [Identité de marque](#2-identité-de-marque)
3. [Logo et symbole](#3-logo-et-symbole)
4. [Couleurs](#4-couleurs)
5. [Typographie](#5-typographie)
6. [Grille, espacement et élévation](#6-grille-espacement-et-élévation)
7. [Iconographie](#7-iconographie)
8. [Composants d'interface](#8-composants-dinterface)
9. [Cartographie](#9-cartographie)
10. [Visualisation de données](#10-visualisation-de-données)
11. [Accessibilité](#11-accessibilité)
12. [Ton éditorial et microcopie](#12-ton-éditorial-et-microcopie)
13. [Tokens de design](#13-tokens-de-design)
14. [Déclinaisons par support](#14-déclinaisons-par-support)
15. [Gouvernance de la charte](#15-gouvernance-de-la-charte)

---

## 1. Philosophie de design

AquaSensus est utilisé debout, sous le soleil, sur un téléphone bas de gamme, par quelqu'un qui a autre chose à faire. Le design ne cherche ni la démonstration technique ni l'effet : il cherche à faire comprendre l'état d'un ouvrage en une seconde et à faire agir en moins d'une minute.

### Cinq principes

| Principe | Ce que cela impose | Ce que cela interdit |
| --- | --- | --- |
| **1. L'état d'abord** | La couleur, la forme et le mot indiquant l'état d'un point d'eau dominent chaque écran | Noyer l'état dans une grille de métadonnées |
| **2. Sobriété data** | Un chiffre est accompagné de son unité, de sa période et de son interprétation | Le tableau de bord décoratif, les graphiques sans légende |
| **3. Terrain d'abord** | Cibles larges, contraste fort, ressources légères, états hors ligne visibles | Animations coûteuses, polices lourdes, images non compressées |
| **4. Explicabilité visible** | Toute alerte affiche sa raison au même niveau visuel que son niveau de gravité | Le score brut affiché seul |
| **5. Dignité** | Vocabulaire respectueux, illustration réaliste et non misérabiliste | L'esthétique « urgence humanitaire », le pathos |

### Le test des trois secondes

Tout écran d'état doit permettre de répondre en trois secondes : **quel ouvrage**, **dans quel état**, **quelle action attendue de moi**. Si un écran échoue à ce test, il est retravaillé avant d'être développé.

---

## 2. Identité de marque

### 2.1 Nom

**AquaSensus** — de *aqua* (eau) et *sensus* (perception, discernement). Le nom porte la promesse du produit : percevoir avant la rupture.

- Écriture : `AquaSensus`, un seul mot, deux capitales internes. Jamais `Aquasensus`, `AQUASENSUS` en corps de texte, ni `Aqua Sensus`.
- Prononciation retenue : *a-koua-sen-suss*.

### 2.2 Signature

**Signature principale :** « Anticiper la panne, garder l'eau. »

Variantes contextuelles autorisées :

| Contexte | Signature |
| --- | --- |
| Institutionnel (ONG, mairie) | « Le suivi partagé des points d'eau communautaires » |
| Académique / soutenance | « Maintenance prédictive des forages communautaires » |
| Terrain / affiche de quartier | « Signalez la panne. Le comité est prévenu. » |

### 2.3 Territoire visuel

Deux registres se répondent :

- **L'eau** — bleu profond, courbes, ondes. Ce que l'on protège.
- **La terre** — ocre de latérite, matière, sol. Le contexte camerounais, l'ouvrage, le réel.

Le vert n'est pas une couleur d'identité : il est réservé à l'état « opérationnel ». Cette discipline évite qu'un aplat décoratif soit lu comme une information d'état.

### 2.4 Ce que la marque n'est pas

| À éviter | Raison |
| --- | --- |
| Esthétique « startup tech » (dégradés violets, néon, 3D) | Contredit l'ancrage terrain et la frugalité |
| Imagerie de détresse (enfants en pleurs, sécheresse spectaculaire) | Contraire au principe de dignité |
| Symbolique institutionnelle lourde (blasons, rubans) | AquaSensus est un outil communautaire, pas une administration |
| Vocabulaire de surveillance (« monitoring des populations », « contrôle ») | L'outil sert les comités, il ne les surveille pas |

---

## 3. Logo et symbole

### 3.1 Concept

Le symbole superpose trois lectures dans une seule forme : une **goutte d'eau**, un **repère de carte** (le point d'eau localisé) et, à l'intérieur, une **onde de signal** en forme d'impulsion — la perception, le *sensus*. La forme extérieure et la ligne intérieure ne doivent jamais être dissociées.

### 3.2 Déclinaisons

| Fichier | Usage |
| --- | --- |
| **`docs/design/aquasensus-logo.png`** | **Logo par défaut de toutes les applications AquaSensus** (1280 × 288 px, fond transparent) |
| `docs/design/aquasensus-logo-640.png` | Même verrouillage en 640 px : en-têtes web, documents, supports légers |
| `docs/design/aquasensus-logo-inverse.png` | Verrouillage inversé (blanc) pour fonds sombres et écrans de démarrage |
| `docs/design/aquasensus-logo.svg` | Source vectorielle du verrouillage horizontal : impression, grands formats |
| `docs/design/aquasensus-mark.svg` | Source vectorielle du symbole seul |
| `docs/design/aquasensus-mark-{16,32,192,512}.png` | Rasters du symbole : favicon, manifeste PWA, icône adaptative |
| `docs/design/render-brand.py` | Régénère tous les rasters depuis la géométrie canonique |

Les rasters ne se retouchent pas à la main : on modifie la géométrie ou les tokens, puis on exécute `python docs/design/render-brand.py`.

### 3.2.1 Règle du logo par défaut

**Toute application, tout front, tout support du projet utilise `aquasensus-logo.png` comme logo par défaut.** Aucun autre fichier ne doit être introduit pour représenter la marque.

| Situation | Fichier à utiliser |
| --- | --- |
| Cas général (en-tête Angular, barre d'application Flutter, écran de connexion, e-mail, rapport, diaporama) | `aquasensus-logo.png` |
| Fond sombre ou écran de démarrage | `aquasensus-logo-inverse.png` |
| Largeur d'affichage < 120 px, ou surface carrée imposée (favicon, icône d'application, marqueur, avatar) | `aquasensus-mark-*.png` — le verrouillage devient illisible en dessous de cette largeur |
| Impression grand format ou besoin de mise à l'échelle sans perte | `aquasensus-logo.svg` |

Les fronts référencent le fichier copié depuis `docs/design/` vers leurs actifs (`frontend-angular/src/assets/brand/`, `mobile-flutter/assets/brand/`). La copie n'est jamais modifiée localement : toute évolution repart de `docs/design/`.

### 3.2.2 Typographie du verrouillage

Le nom est composé en **Inter Bold**. Tant que les fichiers Inter ne sont pas vendorisés dans `docs/design/fonts/`, `render-brand.py` compose le raster avec le repli système déclaré au §5.1 (Segoe UI, puis Arial). Le fichier PNG actuellement livré utilise ce repli. Dès qu'Inter est ajouté au dépôt, relancer le script suffit à produire le verrouillage définitif, sans autre modification.

Variantes de couleur autorisées :

| Variante | Composition | Fond |
| --- | --- | --- |
| Principale | Symbole `#106D99`, onde blanche, texte `#0B4763` | Clair uni |
| Inversée | Symbole blanc, onde `#0B4763`, texte blanc | Foncé ou photographie sombre |
| Monochrome sombre | Tout en `#161E26` | Impression noir et blanc |
| Monochrome clair | Tout en blanc | Fond photographique contrasté |

### 3.3 Règles de construction

- **Zone de protection :** marge libre égale à la hauteur de l'onde intérieure (soit `1/4` de la hauteur du symbole) sur les quatre côtés. Aucun texte, filet ou bord d'image dans cette zone.
- **Taille minimale :** symbole 16 px (favicon), verrouillage horizontal 120 px de large à l'écran, 25 mm à l'impression. En dessous de 24 px, la signature disparaît.
- **Proportions :** le symbole ne se redimensionne que proportionnellement.

### 3.4 Usages interdits

| Interdit | Pourquoi |
| --- | --- |
| Modifier la couleur du symbole hors des variantes listées | Confusion avec un code d'état |
| Colorer le symbole en vert, ambre, orange ou rouge | Ces couleurs sont sémantiques (§4.3) |
| Étirer, incliner, faire pivoter ou appliquer une ombre portée | Dégrade la lisibilité aux petites tailles |
| Remplacer la typographie du nom | Perte de reconnaissance |
| Placer le logo sur une photographie chargée sans voile de contraste | Illisibilité |
| Enfermer le logo dans un cadre ou un badge non prévu | Bruit visuel |

---

## 4. Couleurs

Toutes les valeurs de contraste indiquées sont calculées selon WCAG 2.1 sur fond blanc `#FFFFFF`, sauf mention contraire.

### 4.1 Palette primaire — Bleu Nappe

Couleur de marque, des actions et de la navigation.

| Token | Hex | Usage | Contraste sur blanc |
| --- | --- | --- | --- |
| `blue-50` | `#EFF8FC` | Fonds de section, survol discret | — |
| `blue-100` | `#D6EDF7` | Fond de badge informatif, sélection | — |
| `blue-200` | `#ADDAEF` | Bordures douces, séparateurs accentués | — |
| `blue-300` | `#7BC2E2` | Illustration, dégradés de carte | — |
| `blue-400` | `#45A5D0` | Éléments décoratifs, focus en mode sombre | 2,6:1 |
| `blue-500` | `#1B87B8` | Couleur de marque, symbole, accents | 4,0:1 — composants uniquement |
| **`blue-600`** | **`#106D99`** | **Action principale, liens, boutons** | **5,7:1 — texte AA** |
| `blue-700` | `#0C577C` | Survol d'action, titres de section | 7,7:1 |
| `blue-800` | `#0B4763` | Texte de marque, en-têtes denses | 9,6:1 |
| `blue-900` | `#0A3A52` | Fonds sombres de marque | 11,7:1 |
| `blue-950` | `#06283A` | Fond d'application en mode sombre | 15,1:1 |

**Règle :** `blue-600` est la seule teinte autorisée pour un texte de lien ou un bouton plein avec libellé blanc. `blue-500` reste réservée aux surfaces, icônes et éléments non textuels.

### 4.2 Palette secondaire — Ocre Latérite

Ancrage territorial. Utilisée avec parcimonie : accents éditoriaux, illustrations, séparateurs de section, jamais pour un état.

| Token | Hex | Usage |
| --- | --- | --- |
| `earth-100` | `#F7ECE2` | Fond de bloc éditorial, encadré de citation |
| `earth-300` | `#E2B78E` | Illustration, bordure d'accent |
| `earth-500` | `#C86A2C` | Accent graphique, puce de liste éditoriale |
| `earth-700` | `#A2521F` | Texte d'accent sur fond clair (contraste 5,2:1) |
| `earth-900` | `#6B3512` | Aplats profonds, impression |

### 4.3 Couleurs sémantiques — États des points d'eau

**Ces couleurs sont réservées.** Aucune ne doit apparaître dans un usage décoratif. Chaque état associe obligatoirement une **couleur**, une **forme** et un **libellé** (ENF-43).

| État | Libellé affiché | Couleur pleine | Fond | Texte sur fond | Forme du marqueur | Icône |
| --- | --- | --- | --- | --- | --- | --- |
| `OPERATIONNEL` | Opérationnel | `#1E8E4E` | `#E6F4EC` | `#16713E` | Cercle plein | goutte |
| `SOUS_SURVEILLANCE` | Sous surveillance | `#F2A900` | `#FEF3DC` | `#8A6000` | Cercle à anneau | œil |
| `RISQUE_ELEVE` | Risque élevé | `#D2620E` | `#FDEEE1` | `#A34A08` | Triangle | signal d'alerte |
| `EN_PANNE` | En panne | `#C62828` | `#FCEAEA` | `#A31D1D` | Losange | croix |
| `EN_REPARATION` | En réparation | `#106D99` | `#D6EDF7` | `#0B4763` | Losange à liseré | clé |
| `HORS_SERVICE` | Hors service | `#64748B` | `#EEF2F6` | `#3B4855` | Cercle barré | cadenas |
| *Donnée insuffisante* | Données insuffisantes | `#94A3B3` | `#F7F9FB` | `#51606F` | Cercle pointillé | point d'interrogation |

**Rappel de cohérence :** `EN_REPARATION` reprend le bleu de la marque à dessein — c'est un état d'action en cours, pas un état de gravité. Le rouge signale une privation d'eau réelle, jamais autre chose.

### 4.4 Couleurs sémantiques — Niveaux d'alerte

| Niveau | Couleur | Fond | Usage |
| --- | --- | --- | --- |
| `MODERE` | `#F2A900` | `#FEF3DC` | Fragilité chronique, maintenance en retard |
| `ELEVE` | `#D2620E` | `#FDEEE1` | Seuil d'usage franchi, dégradation progressive |
| `CRITIQUE` | `#C62828` | `#FCEAEA` | Cumul critique, risque de panne imminent |

### 4.5 Couleurs de rétroaction système

| Rôle | Couleur | Usage |
| --- | --- | --- |
| Succès | `#1E8E4E` | Enregistrement, synchronisation réussie, rétablissement confirmé |
| Information | `#106D99` | Message neutre, aide contextuelle |
| Avertissement | `#F2A900` | Donnée imputée, confiance faible, valeur aberrante |
| Erreur | `#C62828` | Échec de validation, refus serveur |
| Hors ligne / en attente | `#64748B` | File locale non synchronisée |

### 4.6 Neutres

| Token | Hex | Usage |
| --- | --- | --- |
| `neutral-0` | `#FFFFFF` | Surface principale claire |
| `neutral-50` | `#F7F9FB` | Fond d'application clair |
| `neutral-100` | `#EEF2F6` | Surface secondaire, ligne alternée |
| `neutral-200` | `#DDE4EB` | Bordures, séparateurs |
| `neutral-300` | `#C3CDD8` | Bordure de champ, état désactivé |
| `neutral-400` | `#94A3B3` | Texte de repos, icône inactive (usage non textuel) |
| `neutral-500` | `#6B7B8C` | Texte tertiaire (4,6:1) |
| `neutral-600` | `#51606F` | Texte secondaire (6,8:1) |
| `neutral-700` | `#3B4855` | Texte courant renforcé (9,5:1) |
| `neutral-800` | `#27313B` | Titres (13,3:1) |
| `neutral-900` | `#161E26` | Texte principal (16,6:1) |

### 4.7 Mode sombre

Utile pour la consultation nocturne et l'économie de batterie. Les couleurs d'état conservent leur signification mais sont éclaircies pour préserver le contraste.

| Rôle | Clair | Sombre |
| --- | --- | --- |
| Fond d'application | `#F7F9FB` | `#06283A` |
| Surface | `#FFFFFF` | `#0F2B3C` |
| Surface élevée | `#FFFFFF` | `#16394D` |
| Bordure | `#DDE4EB` | `#20465C` |
| Texte principal | `#161E26` | `#EAF2F7` |
| Texte secondaire | `#51606F` | `#A7BDCB` |
| Action | `#106D99` | `#45A5D0` |
| Opérationnel | `#1E8E4E` | `#4CC47E` |
| Sous surveillance | `#F2A900` | `#FFC94D` |
| Risque élevé | `#D2620E` | `#FF9B52` |
| En panne | `#C62828` | `#FF7373` |

### 4.8 Règles d'application

1. Ne jamais introduire une couleur hors palette : toute nouvelle nuance passe par une révision de cette charte.
2. Le rapport d'occupation visé sur un écran : environ 70 % neutres, 20 % bleu, 10 % couleurs sémantiques. Un tableau de bord entièrement coloré ne hiérarchise plus rien.
3. Les dégradés sont limités aux illustrations et à l'écran de démarrage ; jamais sur un composant d'interface.
4. Sur photographie, un voile `#0A3A52` à 55 % d'opacité minimum est appliqué avant toute superposition de texte.

---

## 5. Typographie

### 5.1 Familles

| Rôle | Police | Raison | Repli |
| --- | --- | --- | --- |
| Interface et titres | **Inter** | Excellente lisibilité aux petites tailles, chiffres clairs, libre (SIL OFL) | `system-ui, -apple-system, "Segoe UI", Roboto, sans-serif` |
| Données chiffrées, codes, références | **IBM Plex Mono** | Chiffres tabulaires alignés, distinction stricte de `0`/`O` et `1`/`l` pour les codes d'ouvrage | `"SFMono-Regular", Consolas, "Liberation Mono", monospace` |

**Règles de chargement (contrainte ENF-03) :** uniquement les graisses 400, 500, 600 et 700 en `woff2`, sous-ensemble latin, auto-hébergées (aucun appel à un CDN externe), `font-display: swap`. Budget total des polices : **≤ 180 Ko**.

### 5.2 Échelle typographique

Base 16 px, ratio proche de 1,25.

| Style | Taille / interligne | Graisse | Espacement | Usage |
| --- | --- | --- | --- | --- |
| `display` | 40 / 48 px | 700 | −0,02em | Écran d'accueil, couverture de rapport |
| `h1` | 32 / 40 px | 700 | −0,01em | Titre de page |
| `h2` | 24 / 32 px | 600 | −0,01em | Titre de section |
| `h3` | 20 / 28 px | 600 | 0 | Titre de carte, de bloc |
| `h4` | 18 / 26 px | 600 | 0 | Sous-titre, en-tête de liste |
| `body-lg` | 17 / 26 px | 400 | 0 | Texte principal mobile |
| `body` | 16 / 24 px | 400 | 0 | Texte courant |
| `body-sm` | 14 / 21 px | 400 | 0 | Texte secondaire, aide |
| `label` | 14 / 20 px | 500 | 0,01em | Libellé de champ, onglet |
| `caption` | 12 / 16 px | 400 | 0,01em | Légende, horodatage, mention |
| `overline` | 11 / 16 px | 600 | 0,08em, majuscules | Sur-titre de section |
| `metric-xl` | 36 / 40 px | 600, mono | −0,01em | Valeur de carte KPI |
| `metric` | 24 / 28 px | 600, mono | 0 | Valeur secondaire |
| `code` | 14 / 20 px | 500, mono | 0 | Code d'ouvrage, référence d'intervention |

### 5.3 Règles d'usage

1. Taille minimale absolue : **14 px** à l'écran. Le `caption` 12 px est réservé aux mentions non essentielles, jamais à une information d'état.
2. Un seul `h1` par page.
3. Longueur de ligne visée : 60 à 80 caractères ; jamais plus de 90.
4. Pas de texte en majuscules au-delà de 3 mots (hors `overline`).
5. Pas d'italique pour porter une information : réservé aux citations de terrain.
6. Les valeurs numériques comparées en colonne utilisent obligatoirement les chiffres tabulaires (`font-variant-numeric: tabular-nums`).
7. Le texte ne dépend jamais d'une police externe pour rester lisible : le repli système doit produire une mise en page acceptable.

### 5.4 Formats français

| Élément | Format | Exemple |
| --- | --- | --- |
| Date courte | `JJ/MM/AAAA` | `26/08/2026` |
| Date longue | `j mois aaaa` | `26 août 2026` |
| Date et heure | `JJ/MM/AAAA à HH:MM` (Africa/Douala) | `26/08/2026 à 06:40` |
| Durée | Unités explicites, jamais décimales seules | `3 j 14 h` |
| Milliers | Espace insécable | `12 480 L` |
| Décimales | Virgule | `0,85` |
| Pourcentage | Espace insécable avant `%` | `87 %` |
| Volume | Litres, `m³` au-delà de 10 000 L | `12 480 L` / `12,5 m³` |
| Code d'ouvrage | Monospace, majuscules | `YDE-042` |

---

## 6. Grille, espacement et élévation

### 6.1 Espacement

Base **4 px**. Toute marge et tout retrait sont des multiples de cette base.

| Token | Valeur | Usage |
| --- | --- | --- |
| `space-1` | 4 px | Écart icône / libellé |
| `space-2` | 8 px | Interne de badge, écart minimal entre cibles tactiles |
| `space-3` | 12 px | Interne de champ |
| `space-4` | 16 px | Interne de carte, gouttière mobile |
| `space-5` | 20 px | — |
| `space-6` | 24 px | Interne de carte large, écart entre blocs |
| `space-8` | 32 px | Écart entre sections |
| `space-10` | 40 px | — |
| `space-12` | 48 px | Marge de section sur écran large |
| `space-16` | 64 px | Respiration de page |

### 6.2 Points de rupture et grille

| Nom | Largeur | Colonnes | Gouttière | Marge |
| --- | --- | --- | --- | --- |
| `xs` | < 480 px | 4 | 16 px | 16 px |
| `sm` | ≥ 480 px | 4 | 16 px | 24 px |
| `md` | ≥ 768 px | 8 | 24 px | 32 px |
| `lg` | ≥ 1024 px | 12 | 24 px | 40 px |
| `xl` | ≥ 1280 px | 12 | 32 px | 48 px |
| `2xl` | ≥ 1536 px | 12 | 32 px | auto, contenu limité à 1440 px |

**Conception mobile d'abord.** Le parcours de signalement est dessiné pour `xs` puis élargi. Sur `lg` et au-delà, le tableau de bord adopte une disposition carte à gauche (8 colonnes) / file de travail à droite (4 colonnes).

### 6.3 Rayons de bordure

| Token | Valeur | Usage |
| --- | --- | --- |
| `radius-sm` | 4 px | Badge, puce, petit marqueur |
| `radius-md` | 8 px | Bouton, champ, menu |
| `radius-lg` | 12 px | Carte, boîte de dialogue |
| `radius-xl` | 16 px | Feuille modale mobile, grand panneau |
| `radius-pill` | 999 px | Filtre, étiquette d'état |
| `radius-full` | 50 % | Avatar, pastille |

### 6.4 Élévation

Ombres discrètes et froides. Aucune ombre colorée.

| Token | Valeur | Usage |
| --- | --- | --- |
| `elev-0` | aucune | Contenu à plat, séparé par une bordure |
| `elev-1` | `0 1px 2px rgba(11,71,99,.08)` | Carte au repos |
| `elev-2` | `0 2px 8px rgba(11,71,99,.10)` | Carte survolée, barre supérieure |
| `elev-3` | `0 8px 24px rgba(11,71,99,.14)` | Menu, infobulle de carte, bouton flottant |
| `elev-4` | `0 16px 40px rgba(11,71,99,.18)` | Boîte de dialogue, feuille modale |

En mode sombre, l'élévation se traduit par un éclaircissement de surface plutôt que par une ombre.

### 6.5 Mouvement

| Token | Durée | Courbe | Usage |
| --- | --- | --- | --- |
| `motion-fast` | 120 ms | `cubic-bezier(.4,0,.2,1)` | Survol, focus, bascule |
| `motion-base` | 200 ms | `cubic-bezier(.4,0,.2,1)` | Ouverture de panneau, apparition de carte |
| `motion-slow` | 320 ms | `cubic-bezier(.2,0,0,1)` | Feuille modale, transition de page |

Aucune animation ne dépasse 400 ms. `prefers-reduced-motion: reduce` supprime toute transition non essentielle. Aucune animation en boucle continue, hors indicateur de synchronisation actif.

---

## 7. Iconographie

### 7.1 Style

Jeu de base : **Material Symbols Rounded** (libre, poids variable), style *outlined*, graisse 400, taille optique 24. Le jeu est auto-hébergé et sous-ensemblé aux seules icônes utilisées.

| Règle | Valeur |
| --- | --- |
| Grille de dessin | 24 × 24 px |
| Épaisseur de trait | 2 px à 24 px, ajustée proportionnellement |
| Terminaisons | Arrondies |
| Tailles autorisées | 16, 20, 24, 32, 48 px |
| Couleur | Hérite de la couleur de texte, ou couleur sémantique d'état |

### 7.2 Icônes métier de référence

| Concept | Icône | Notes |
| --- | --- | --- |
| Point d'eau / forage | goutte dans un repère | Base du marqueur cartographique |
| Mini-réseau | réseau de nœuds reliés | Distinguer du forage isolé |
| Signalement | mégaphone | Jamais une cloche (réservée aux notifications) |
| Intervention | clé à molette | — |
| Technicien | casque de chantier | — |
| Comité / délégué | groupe de personnes | — |
| Charge d'usage / échéance | sablier | Estimation, jamais une mesure de litres |
| Indice de santé | cœur d'activité (impulsion) | Rappelle l'onde du symbole |
| Alerte prédictive | triangle avec impulsion | Se distingue de l'erreur système |
| Temps de rétablissement | chronomètre | KPI principal |
| Hors ligne / en attente | nuage barré | — |
| SMS / USSD | bulle de message | — |

### 7.3 Règles

1. Une icône seule ne porte jamais une action critique : elle est accompagnée d'un libellé ou, à défaut, d'un `aria-label` et d'une infobulle.
2. Une même icône ne désigne jamais deux concepts différents dans le produit.
3. Les icônes d'état reprennent la forme définie au §4.3 (cercle, triangle, losange), afin que l'information reste lisible en niveaux de gris.
4. Pas d'émoji dans l'interface, les notifications ou les SMS.

---

## 8. Composants d'interface

### 8.1 Boutons

| Variante | Fond | Texte | Bordure | Usage |
| --- | --- | --- | --- | --- |
| Principal | `#106D99` | `#FFFFFF` | — | Action unique et attendue de l'écran |
| Secondaire | `#FFFFFF` | `#106D99` | 1 px `#106D99` | Action alternative |
| Discret | transparent | `#3B4855` | — | Action tertiaire, annulation |
| Danger | `#C62828` | `#FFFFFF` | — | Déclarer une panne totale, annuler une intervention |
| Succès | `#1E8E4E` | `#FFFFFF` | — | Confirmer un rétablissement |

| Taille | Hauteur | Retrait horizontal | Style de texte |
| --- | --- | --- | --- |
| Grand (terrain, action primaire mobile) | 56 px | 24 px | `body-lg` 600 |
| Moyen (défaut) | 48 px | 20 px | `body` 600 |
| Petit (barre d'outils dense, bureau) | 40 px | 16 px | `body-sm` 600 |

**États :** survol = teinte −100 (`blue-700`) ; focus = contour 2 px `#45A5D0` avec décalage 2 px ; actif = teinte −200 ; désactivé = `neutral-200` sur `neutral-400`, curseur interdit ; chargement = libellé conservé, indicateur circulaire à gauche, bouton non cliquable.

**Règles :** un seul bouton principal par écran. Sur mobile, l'action principale d'un parcours de saisie est ancrée en bas, pleine largeur, hauteur 56 px. Les libellés sont des verbes à l'infinitif (« Signaler une panne », « Confirmer le rétablissement »), jamais « OK » ni « Valider » seul.

### 8.2 Champs de saisie

| Propriété | Valeur |
| --- | --- |
| Hauteur | 48 px (56 px sur les écrans de saisie terrain) |
| Retrait interne | 12 px vertical, 16 px horizontal |
| Bordure | 1 px `#C3CDD8`, `radius-md` |
| Focus | Bordure 2 px `#106D99` + halo `rgba(16,109,153,.15)` |
| Erreur | Bordure 2 px `#C62828`, message `body-sm` `#A31D1D` sous le champ, icône d'erreur |
| Libellé | `label` au-dessus du champ, toujours visible — **pas de libellé flottant seul** |
| Aide | `caption` `#51606F` sous le champ |
| Obligatoire | Astérisque `#C62828` + mention textuelle en tête de formulaire |

Les champs de saisie de terrain privilégient les sélecteurs à gros boutons (catégorie de symptôme, gravité) plutôt que des listes déroulantes. **Aucun champ de volume, de bidon, de seau ou de durée de pompage.**

### 8.3 Étiquette d'état (badge)

Élément le plus important du produit.

| Propriété | Valeur |
| --- | --- |
| Structure | icône de forme + libellé textuel |
| Hauteur | 24 px (compact) / 32 px (standard) |
| Retrait | 8 px horizontal, 4 px vertical |
| Rayon | `radius-pill` |
| Couleurs | Fond et texte du §4.3, jamais d'autre combinaison |
| Typographie | `body-sm` graisse 600 |
| Bordure | 1 px de la couleur pleine à 30 % d'opacité |

**Interdit :** une pastille de couleur sans libellé, un badge d'état en couleur de marque, un état abrégé (« OP », « HS »).

### 8.4 Carte de point d'eau

Composant récurrent (liste, carte géographique, tableau de bord).

```
┌──────────────────────────────────────────────┐
│ [icône état]  Forage Nkolbisson Marché       │
│               YDE-042 · Nkolbisson           │
│                                              │
│  ● Sous surveillance      Indice 68 / 100    │
│                                              │
│  Dernier rétablissement : 12/07/2026         │
│  3 signalements en 21 jours                  │
│                                              │
│  [ Voir la fiche ]        [ Signaler ]       │
└──────────────────────────────────────────────┘
```

| Zone | Règle |
| --- | --- |
| Bandeau latéral | Filet de 4 px à la couleur de l'état, sur toute la hauteur à gauche |
| Titre | `h4`, nom d'usage — jamais le code seul |
| Sous-titre | `caption` mono pour le code, puis localité |
| État | Étiquette §8.3, position constante |
| Indice | `metric` avec mention « / 100 » et pastille de confiance si `FAIBLE` |
| Faits | Deux lignes maximum, les plus décisives pour l'action |
| Actions | Deux au maximum ; sur mobile, pleine largeur empilées |
| Élévation | `elev-1`, `elev-2` au survol, `radius-lg` |

### 8.5 Encart d'alerte prédictive

L'explication a le même poids visuel que la gravité (principe 4).

| Zone | Contenu |
| --- | --- |
| En-tête | Icône du niveau + « Risque élevé » + horizon (« sous 14 jours ») |
| Corps | Phrase d'explication en `body`, jamais un score seul |
| Facteurs | Trois lignes maximum : libellé, valeur observée, seuil, barre de contribution |
| Recommandation | Bloc `body-sm` sur fond `neutral-50` |
| Actions | « Créer une intervention préventive » (principal), « Reporter », « Contester » |
| Pied | Date d'émission et version de paramétrage en `caption` |

Le fond est la teinte claire du niveau, la bordure gauche de 4 px à la couleur pleine.

### 8.6 Chronologie d'intervention

Fil vertical présentant les transitions (`OUVERTE` → `CLOTUREE`). Chaque nœud : pastille colorée selon le statut, libellé de l'étape, horodatage `caption`, auteur. Les étapes futures sont en `neutral-300` pointillé. La durée entre deux nœuds est affichée sur le segment (`+ 2 j 4 h`). Le segment total, du signalement à la clôture, est mis en évidence comme **temps de rétablissement**.

### 8.7 Carte KPI

| Zone | Règle |
| --- | --- |
| Sur-titre | `overline` `neutral-500` — nom de l'indicateur |
| Valeur | `metric-xl` mono `neutral-900` |
| Unité | `body-sm` `neutral-600`, accolée |
| Comparaison | Flèche + variation + période de référence, couleur sémantique **selon le sens métier** (une baisse du temps de rétablissement est un succès, donc verte) |
| Contexte | `caption`, période et volumétrie (« sur 34 interventions ») |
| Interdit | Valeur sans période, variation sans base de comparaison |

### 8.8 Tableaux

En-tête `label` sur `neutral-100`, en-tête figé au défilement, lignes de 48 px minimum, séparateurs 1 px `neutral-200`, survol `blue-50`, chiffres alignés à droite en tabulaire, texte à gauche, colonne d'état non tronquée. Sur `xs` et `sm`, les tableaux se transforment en liste de cartes : aucun défilement horizontal.

### 8.9 États vides, chargement et erreurs

| Situation | Traitement |
| --- | --- |
| Liste vide (aucun signalement) | Illustration au trait simple, phrase positive (« Aucun signalement en attente. »), action utile si pertinente |
| Chargement | Squelettes gris respectant la forme du contenu attendu ; jamais de rotative plein écran au-delà de 300 ms |
| Erreur réseau | Message expliquant la cause, action « Réessayer », rappel que les saisies locales sont conservées |
| Hors ligne | Bandeau persistant `neutral-600` en haut : « Hors ligne — 3 éléments seront envoyés à la reconnexion » |
| Donnée insuffisante | Bloc neutre expliquant pourquoi l'indice n'est pas calculé, jamais un « 0 » trompeur |

### 8.10 Notifications transitoires et boîtes de dialogue

Les messages transitoires apparaissent en bas sur mobile, en haut à droite sur bureau ; durée 5 s, 8 s si une action « Annuler » est proposée ; ils ne masquent jamais l'action principale. Une confirmation destructive ou irréversible passe obligatoirement par une boîte de dialogue nommant explicitement l'objet concerné (« Annuler l'intervention INT-2026-0134 ? »).

### 8.11 Navigation

| Contexte | Modèle |
| --- | --- |
| Mobile | Barre inférieure de 4 entrées maximum : Carte, Signaler, Interventions, Profil. Hauteur 56 px + zone sûre. |
| Bureau | Barre latérale rétractable de 264 px, groupée par domaine, avec pastille de compteur sur la file de travail. |
| Fil d'Ariane | Sur les fiches profondes uniquement, `body-sm` |

L'action « Signaler » est mise en avant (bouton central proéminent sur mobile) : c'est la porte d'entrée de la valeur sociale du produit.

---

## 9. Cartographie

### 9.1 Fond de carte

Fond clair et désaturé (OpenStreetMap restylé ou tuiles claires équivalentes, licence libre respectée et attribution affichée). Aucun relief marqué, aucune couleur saturée : le fond ne doit jamais concurrencer les marqueurs d'état.

### 9.2 Marqueurs

| Propriété | Valeur |
| --- | --- |
| Taille | 32 px au repos, 40 px sélectionné |
| Composition | Forme du §4.3 + icône blanche + halo blanc 2 px |
| Ombre | `elev-2` pour le décollement du fond |
| Sélection | Anneau `blue-600` de 3 px, marqueur agrandi |
| Alerte active | Pastille d'alerte en surcharge, coin supérieur droit |
| Hors service | Opacité 60 %, forme barrée |

**La forme prime sur la couleur :** un utilisateur daltonien doit distinguer les états par la seule silhouette (§11).

### 9.3 Regroupement

Au-delà de 50 marqueurs visibles, regroupement par proximité. Le regroupement est un disque neutre `blue-700` affichant le nombre d'ouvrages, entouré d'une couronne segmentée proportionnelle aux états présents. Un regroupement contenant au moins un ouvrage `EN_PANNE` porte un liseré rouge extérieur.

### 9.4 Légende et contrôles

Légende permanente et non rétractable sur bureau, accessible en une touche sur mobile (feuille modale). Elle liste les six états avec forme, couleur et libellé. Contrôles : zoom, recentrage sur ma position, filtres d'état, bascule liste / carte. Tous les contrôles respectent la cible tactile de 48 px.

---

## 10. Visualisation de données

### 10.1 Principes

1. Un graphique répond à une question métier explicite, formulée dans son titre.
2. Toujours indiquer la période, l'unité et le nombre d'observations.
3. Les données imputées ou estimées sont visuellement distinctes (trait pointillé, hachure) et mentionnées en légende.
4. Axe des ordonnées démarrant à zéro pour toute comparaison d'effectifs ou de durées.
5. Pas de camembert au-delà de 4 catégories ; préférer une barre empilée horizontale.
6. Pas de troisième dimension, pas d'effet de perspective.

### 10.2 Palettes de graphiques

**Séquentielle (indice de santé, intensité) :** `#D6EDF7` → `#7BC2E2` → `#1B87B8` → `#106D99` → `#0B4763`

**Catégorielle (jusqu'à 6 séries, distinguable en niveaux de gris) :** `#106D99`, `#C86A2C`, `#1E8E4E`, `#7B5EA7`, `#64748B`, `#0B4763`

**Comparative d'état :** obligatoirement les couleurs sémantiques du §4.3, dans l'ordre canonique opérationnel → surveillance → risque → panne → réparation → hors service.

### 10.3 Graphiques de référence

| Question métier | Représentation | Règles |
| --- | --- | --- |
| Quel est l'état du parc ? | Barre empilée horizontale à 100 % | Couleurs d'état, valeurs absolues en infobulle |
| Combien de temps pour rétablir ? | Boîte à moustaches ou barres médiane/P90 par mois | Toujours afficher l'effectif |
| L'échéance d'entretien approche-t-elle ? | Barre de charge cumulée / intervalle effectif | Libellé « estimation », jamais d'unité en litres |
| Quelle est la santé d'un ouvrage ? | Jauge segmentée 0–100 aux bandes du §4.3 | Score chiffré + libellé de bande + confiance |
| Anticipe-t-on les pannes ? | Chronologie alertes / pannes | Alerte = repère triangulaire, panne = repère losange, lien visuel entre les deux |
| Où se concentrent les pannes ? | Carte + histogramme par quartier | Ne jamais utiliser une carte de chaleur qui masquerait les ouvrages |

### 10.4 Composants annexes

Infobulle : fond `neutral-900` à 95 %, texte blanc, `radius-md`, `elev-3`, valeur en mono. Légende : au-dessus du graphique sur mobile, à droite sur bureau, forme + libellé. Grille : lignes horizontales 1 px `neutral-200`, aucune ligne verticale sauf séries temporelles denses.

---

## 11. Accessibilité

Référence : WCAG 2.1 niveau AA (ENF-40 à ENF-46).

### 11.1 Exigences appliquées

| Domaine | Règle |
| --- | --- |
| Contraste texte | ≥ 4,5:1 (≥ 3:1 pour texte ≥ 24 px ou ≥ 19 px gras) |
| Contraste composants | ≥ 3:1 pour bordures de champ, icônes porteuses de sens, marqueurs |
| Cibles tactiles | ≥ 48 × 48 px, espacement ≥ 8 px |
| Focus | Contour visible 2 px `#45A5D0`, décalage 2 px, jamais supprimé |
| Clavier | Tout parcours réalisable sans souris, ordre de tabulation logique, lien d'évitement |
| Sémantique | Titres hiérarchisés, listes réelles, `aria-live` sur les mises à jour d'état, formulaires étiquetés |
| Zoom | Aucun défilement horizontal jusqu'à 200 % |
| Mouvement | `prefers-reduced-motion` respecté |
| Langue | `lang="fr"` déclaré ; abréviations explicitées à la première occurrence |

### 11.2 Indépendance à la couleur

L'information d'état est toujours portée par **trois canaux** : couleur, forme et libellé. Test de validation obligatoire avant chaque livraison : convertir les captures en niveaux de gris ; si un état devient ambigu, le composant est refusé.

| État | Couleur | Forme | Libellé |
| --- | --- | --- | --- |
| Opérationnel | Vert | Cercle plein | « Opérationnel » |
| Sous surveillance | Ambre | Cercle à anneau | « Sous surveillance » |
| Risque élevé | Orange | Triangle | « Risque élevé » |
| En panne | Rouge | Losange | « En panne » |
| En réparation | Bleu | Losange à liseré | « En réparation » |
| Hors service | Gris | Cercle barré | « Hors service » |

### 11.3 Conditions de terrain

| Contrainte | Réponse de conception |
| --- | --- |
| Lecture en plein soleil | Contrastes renforcés, aplats pleins plutôt que dégradés, texte ≥ 16 px sur les écrans terrain |
| Utilisation à une main, écran 5" | Actions principales dans le tiers inférieur, barre d'action ancrée |
| Mains mouillées ou sales, gants | Cibles 56 px sur les parcours de saisie terrain, marge d'erreur au toucher |
| Faible littératie numérique | Icône + texte systématiques, vocabulaire courant, parcours linéaires sans branche cachée |
| Réseau instable | État de synchronisation toujours visible, aucune action silencieusement perdue |
| Batterie faible | Mode sombre disponible, pas d'animation continue |

### 11.4 Contrôles

Automatisés (axe-core) sur les parcours critiques dans l'intégration continue, complétés par une revue manuelle au clavier et un test de lisibilité en extérieur avant chaque jalon.

---

## 12. Ton éditorial et microcopie

### 12.1 Voix

Claire, factuelle, respectueuse. AquaSensus s'adresse à des adultes responsables d'un bien commun.

| Nous écrivons | Nous n'écrivons pas |
| --- | --- |
| « Le comité a été averti. » | « Votre requête a été soumise avec succès au système. » |
| « Risque de panne élevé sous 14 jours. » | « Alerte critique !!! Défaillance imminente. » |
| « 3 signalements de débit faible en 21 jours. » | « Anomalies multiples détectées. » |
| « Aucun signalement en attente. » | « Rien à afficher. » |
| « Impossible d'envoyer maintenant. Le signalement sera transmis dès le retour du réseau. » | « Erreur 503. » |

### 12.2 Règles rédactionnelles

1. Phrases courtes, voix active, présent de l'indicatif.
2. Vocabulaire du glossaire du cahier des charges, sans variation synonymique : on écrit toujours « point d'eau », « signalement », « intervention », « comité ».
3. Aucun jargon technique dans l'interface destinée aux usagers et aux délégués (pas de « endpoint », « payload », « score composite »).
4. Un message d'erreur dit toujours : ce qui s'est passé, la conséquence, l'action possible.
5. Les libellés de boutons sont des verbes d'action à l'infinitif.
6. Ne jamais culpabiliser l'utilisateur : c'est le système qui échoue, pas la personne.
7. Aucun émoji, aucune ponctuation expressive multiple.
8. Les personnes ne sont pas des « bénéficiaires passifs » : ce sont des habitants, des délégués, des techniciens.

### 12.3 Microcopie de référence

| Contexte | Texte |
| --- | --- |
| Confirmation de signalement | « Signalement enregistré. Référence SIG-2026-01187. Le comité a été averti. » |
| Signalement déjà connu | « Déjà signalé par 6 personnes. Une intervention est en préparation. » |
| Hors ligne | « Hors ligne. 3 éléments seront envoyés dès le retour du réseau. » |
| Alerte prédictive | « Risque de panne élevé sous 14 jours. Voir les raisons. » |
| Confiance faible | « Indice fondé sur une fiche incomplète : fiabilité limitée. Compléter la population desservie. » |
| Rétablissement | « Eau rétablie le 26/08/2026. Durée d'interruption : 3 j 14 h. » |
| Refus d'accès | « Vous n'avez pas accès à ce point d'eau. Contactez votre comité. » |

### 12.4 Contraintes SMS et USSD

| Règle | Valeur |
| --- | --- |
| Longueur | ≤ 160 caractères, message unique |
| Alphabet | GSM-7 uniquement : pas d'émoji, ni de caractère rare |
| Accents | Tolérés à la lecture, évités à l'écriture (`enregistre` plutôt que `enregistré`) pour préserver le budget de caractères |
| Structure | Marque, fait, action, référence |
| Identification | Tout message sortant commence par `AquaSensus:` |

Exemples :

```
AquaSensus: signalement SIG-2026-01187 enregistre pour YDE-042.
Le comite est averti. Repondez STOP pour ne plus recevoir de message.

AquaSensus: risque de panne eleve sur YDE-042 sous 14 jours.
Motif: debit faible signale 5 fois en 21 jours. Prevoir une inspection.
```

---

## 13. Tokens de design

Les tokens sont la source d'application de cette charte. **Aucune valeur brute (hex, px) ne doit être écrite dans le code applicatif** : on référence toujours un token. Les fichiers `docs/design/tokens.css` et `docs/design/tokens.dart` font foi pour l'implémentation et sont maintenus synchronisés avec ce document.

### 13.1 Convention de nommage

```
--aqs-<catégorie>-<élément>[-<variante>][-<état>]
```

| Catégorie | Exemples |
| --- | --- |
| `color` | `--aqs-color-action`, `--aqs-color-state-en-panne` |
| `space` | `--aqs-space-4` |
| `radius` | `--aqs-radius-lg` |
| `font` | `--aqs-font-sans`, `--aqs-font-size-h2` |
| `elev` | `--aqs-elev-2` |
| `motion` | `--aqs-motion-base` |

Deux niveaux : les tokens **primitifs** portent la valeur (`--aqs-color-blue-600`), les tokens **sémantiques** portent l'intention (`--aqs-color-action: var(--aqs-color-blue-600)`). Les composants ne consomment que des tokens sémantiques, ce qui rend le mode sombre et les évolutions de palette sans risque.

### 13.2 Extrait — variables CSS (Angular)

```css
:root {
  /* Primitifs */
  --aqs-color-blue-600: #106D99;
  --aqs-color-blue-700: #0C577C;
  --aqs-color-neutral-900: #161E26;

  /* Sémantiques */
  --aqs-color-action: var(--aqs-color-blue-600);
  --aqs-color-action-hover: var(--aqs-color-blue-700);
  --aqs-color-text: var(--aqs-color-neutral-900);

  /* États des points d'eau */
  --aqs-color-state-operationnel: #1E8E4E;
  --aqs-color-state-surveillance: #F2A900;
  --aqs-color-state-risque: #D2620E;
  --aqs-color-state-panne: #C62828;
  --aqs-color-state-reparation: #106D99;
  --aqs-color-state-hors-service: #64748B;

  --aqs-space-4: 16px;
  --aqs-radius-lg: 12px;
  --aqs-elev-1: 0 1px 2px rgba(11, 71, 99, 0.08);
  --aqs-motion-base: 200ms cubic-bezier(0.4, 0, 0.2, 1);
}
```

Le fichier complet, incluant le thème sombre (`[data-theme="dark"]`), se trouve dans `docs/design/tokens.css`.

### 13.3 Extrait — tokens Flutter

```dart
class AqsColors {
  static const action = Color(0xFF106D99);
  static const stateOperationnel = Color(0xFF1E8E4E);
  static const statePanne = Color(0xFFC62828);
}
```

Le fichier complet, incluant l'échelle typographique et la construction du `ThemeData`, se trouve dans `docs/design/tokens.dart`.

### 13.4 Règle d'implémentation

| Cible | Application |
| --- | --- |
| Angular | `tokens.css` importé une seule fois à la racine des styles ; interdiction d'écrire une couleur littérale dans un composant |
| Flutter | `AqsTheme.light()` / `AqsTheme.dark()` appliqués au `MaterialApp` ; aucune `Color(0x...)` hors du fichier de tokens |
| Documents et supports | Mêmes valeurs hexadécimales, reportées telles quelles |

---

## 14. Déclinaisons par support

### 14.1 Application web (PWA)

| Élément | Spécification |
| --- | --- |
| Logo d'en-tête | `aquasensus-logo.png` (ou `aquasensus-logo-640.png` selon la densité), hauteur d'affichage 32 px |
| Icônes d'application | `aquasensus-mark-192.png` et `aquasensus-mark-512.png`, plus une variante *maskable* avec zone sûre de 40 % |
| Couleur de thème | `#106D99` |
| Couleur de fond du manifeste | `#F7F9FB` |
| Écran de démarrage | `aquasensus-logo-inverse.png` centré sur `#0B4763` |
| Favicon | `aquasensus-mark.svg` + repli `aquasensus-mark-32.png` |
| Titre de page | `<Page> · AquaSensus` |
| Budget | ≤ 1,5 Mo au premier chargement (ENF-03) : polices sous-ensemblées, icônes sous-ensemblées, images en WebP, aucune bibliothèque graphique lourde |

### 14.2 Application mobile (Flutter)

Logo par défaut dans la barre d'application et sur l'écran de connexion : `aquasensus-logo.png`, déposé dans `mobile-flutter/assets/brand/` et déclaré au `pubspec.yaml`. Icône adaptative Android : `aquasensus-mark-512.png` sur fond `#0B4763`, zone sûre respectée. Écran de démarrage : `aquasensus-logo-inverse.png`. Barre d'état harmonisée avec la surface, respect des zones sûres, des gestes système et du retour arrière natif. Mêmes tokens, mêmes libellés que la PWA : aucun écart de vocabulaire entre les deux fronts.

### 14.3 Supports imprimés et terrain

| Support | Règles |
| --- | --- |
| Fiche d'ouvrage (A4, N&B) | Code d'ouvrage en gros caractères, état en forme + libellé (jamais en couleur seule), historique sur 12 mois, échéance d'entretien estimée, cadre de notes manuscrites (pas de relevé de volume) |
| Affiche de quartier (A3) | Signature « Signalez la panne. Le comité est prévenu. », code SMS et syntaxe en très gros caractères, numéro court, symbole en haut |
| Autocollant d'ouvrage | Symbole + code `YDE-042` + syntaxe SMS ; lisible à 2 m ; matériau résistant au soleil |

Chaque support porte le logo par défaut (`aquasensus-logo.png`, ou `aquasensus-logo.svg` au-delà du A3). Toute production imprimée doit rester lisible en photocopie noir et blanc : c'est la condition d'usage réelle sur le terrain.

### 14.4 Supports de présentation (jury, partenaires)

Fond clair `#F7F9FB` ou sombre `#0A3A52`, jamais de fond photographique sous du texte sans voile. Un message par diapositive, titre en `h1`, chiffres en `metric-xl` mono. Les captures d'interface sont présentées sans cadre décoratif ni perspective. Les couleurs d'état sont toujours accompagnées de leur légende, y compris en présentation.

---

## 15. Gouvernance de la charte

### 15.1 Portée normative

Cette charte est **contraignante** pour toute interface AquaSensus. Un composant qui s'en écarte ne passe pas la revue (`Definition of Done`, §17.1 du cahier des charges).

### 15.2 Liste de contrôle avant livraison d'un écran

1. Toutes les couleurs proviennent de tokens sémantiques ; aucune valeur littérale.
2. Chaque état affiche couleur + forme + libellé ; la capture reste compréhensible en niveaux de gris.
3. Les cibles tactiles atteignent 48 px, 56 px sur les parcours terrain.
4. Un seul bouton principal ; libellés à l'infinitif.
5. Les chiffres portent unité, période et contexte.
6. Les états vide, chargement, erreur et hors ligne sont définis.
7. La navigation clavier fonctionne, le focus est visible.
8. Le texte respecte le §12 : pas de jargon, pas d'émoji, vocabulaire du glossaire.
9. Le budget de ressources est respecté (polices et icônes sous-ensemblées).
10. Le logo affiché est l'actif par défaut `aquasensus-logo.png` (ou la variante prévue au §3.2.1), non retouché.
11. L'écran passe le test des trois secondes (§1).

### 15.3 Processus d'évolution

Toute demande d'évolution (nouvelle couleur, nouveau composant, nouvelle règle) est d'abord justifiée par un besoin d'usage, puis intégrée à ce document **avant** implémentation, avec incrément de version et entrée dans l'historique des révisions. Les fichiers de `docs/design/` sont mis à jour dans le même mouvement. En cas d'écart entre le code et la charte, c'est le code qui est corrigé, sauf décision explicite documentée ici.

### 15.4 Attributions et licences

| Ressource | Licence |
| --- | --- |
| Inter | SIL Open Font License 1.1 |
| IBM Plex Mono | SIL Open Font License 1.1 |
| Material Symbols | Apache License 2.0 |
| Fond de carte | OpenStreetMap — ODbL, attribution affichée en permanence sur la carte |

Toutes les ressources retenues sont libres et redistribuables, conformément à l'engagement open-source du projet.

---

*Fin de la charte graphique AQS-CHG-001 v1.1.*
