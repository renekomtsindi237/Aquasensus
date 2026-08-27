# M2 — Signalement d'incident

**Lot :** L1 · **Paquet Java :** `reporting`  
**Exigences :** EF-10 à EF-17 · **Règles :** RG-02, RG-03, RG-11, RG-14  
**Diagrammes :** `UC2-signalement.puml`, `CL3-signalement.puml`, `AC1`, `AC2`, `SQ2`, `ET2`

Le signalement est le **capteur humain**. Pas de volume, pas de compteur.

---

## ISS-016 — Créer un signalement en moins de 60 s

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Must |
| Réf. | EF-10, EF-12, EF-97, ENF-02 |

**Objectif :** 4 écrans max, taxonomie fermée, `X-Client-Request-Id`, commentaire et photo optionnels.

**Acceptation**
- [ ] `POST /reports` ; 201 + référence lisible.
- [ ] Rejeu du même UUID → 200, pas de doublon.
- [ ] Hors ligne : file locale (dépend ISS-059).
- [ ] Tests de chaque valeur de `CategorieSymptome`.

---

## ISS-017 — Signalement sans compte

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Must |
| Réf. | EF-11, ENF-24, RG-10 |

**Objectif :** Téléphone + code simulé, quota 5/heure/numéro, pas de KYC.

**Acceptation**
- [ ] Quota dépassé → 429 expliqué.
- [ ] Numéro haché en base, 4 derniers chiffres seuls en restitution.
- [ ] Pas d'énumération (« ce numéro n'existe pas »).

---

## ISS-018 — Corroboration 24 h

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Must |
| Réf. | EF-13, RG-03 |

**Objectif :** Même ouvrage + même catégorie dans la fenêtre = un incident, pas deux.

**Acceptation**
- [ ] Compteur incrémenté, parent renseigné.
- [ ] Hors fenêtre ou catégorie différente → nouvel incident.
- [ ] Concurrence : deux POST simultanés ne créent pas deux références (verrouillage optimiste + unicité).

**TDD :** RG-03 en test unitaire dédié.

---

## ISS-019 — État de prise en charge immédiat

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Must |
| Réf. | EF-14 |

**Objectif :** Le déclarant voit « déjà signalé par N personnes » / intervention en cours — jamais un silence.

**Acceptation**
- [ ] Bloc `prise_en_charge` dans la réponse (CDC §8.3).
- [ ] Libellés en français sobre (charte §12).

---

## ISS-020 — Qualifier, rejeter, doublon

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Must |
| Réf. | EF-15, RG-11 |

**Objectif :** Délégué du périmètre uniquement. Rejet **motivé**, sans effet sur l'état ni l'indice.

**Acceptation**
- [ ] `PATCH /reports/{id}/qualification`.
- [ ] Test RG-11 : rejet → ouvrage inchangé.
- [ ] 403 hors périmètre.

---

## ISS-021 — Priorité automatique et gel manuel

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Should |
| Réf. | EF-16, RG-14 |

**Objectif :** Gravité × corroborations × population ; ajustement délégué avec justification gèle le recalcul.

**Acceptation**
- [x] Recalcul à chaque corroboration si non figé.
- [x] Modification manuelle journalisée (`PATCH /reports/{id}/priorite`).

---

## ISS-022 — Bascule EN_PANNE sur panne totale confirmée

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Must |
| Réf. | RG-02, EF-04 |

**Objectif :** `PANNE_TOTALE` + 2 corroborations **ou** qualification délégué → ouvrage `EN_PANNE`, historisé.

**Acceptation**
- [x] Un seul signalement non corroboré non qualifié ne bascule pas.
- [x] Test unitaire RG-02.
