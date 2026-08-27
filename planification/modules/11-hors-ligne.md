# M11 — Mode hors ligne et synchronisation

**Lots :** L1 (file + idempotence dès le signalement) · L4 (PWA installable aboutie, conflits)  
**Exigences :** EF-95 à EF-99 · **ENF-12** · **QA-5**  
**Diagrammes :** `AC7-synchronisation-hors-ligne.puml`, `SQ6-intervention-hors-ligne.puml`

Types d'opérations en file : signalement, transition, compte rendu — **pas de relevé de volume**.

---

## ISS-058 — PWA installable et cache du périmètre

| Champ | Valeur |
| --- | --- |
| Statut | Fait (partiel) |
| Lot | L1 (base) / L4 (manifeste, splash, logo) |
| Priorité | Must |
| Réf. | EF-95, ENF-03 |

**Objectif :** Installable ; hors ligne on consulte les ouvrages et interventions **déjà chargés**.

**Acceptation**
- [x] Manifeste + service worker (`public/sw.js`).
- [x] Logo / icônes : `aquasensus-logo.png` et `aquasensus-mark-*.png`.
- [x] Bandeau « Hors ligne » visible (charte).

---

## ISS-059 — File locale persistante et idempotence

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L1 |
| Priorité | Must |
| Réf. | EF-96, EF-97, ENF-12 |

**Objectif :** UUID client à la saisie, FIFO, rejeu auto, temporisation croissante. Double filet serveur (recherche + unicité).

**Acceptation**
- [x] Coupure : brouillon sessionStorage + file locale avec le même `X-Client-Request-Id`.
- [x] Rejeu → pas de doublon (`SignalementApiTest` 201 puis 200).
- [x] Flutter : en-tête `X-Client-Request-Id` (même contrat).

**TDD :** QA-5 scénarios coupure / rejeu / doublon.

---

## ISS-060 — États de sync visibles et conflits

| Champ | Valeur |
| --- | --- |
| Statut | Fait (affichage) |
| Lot | L4 |
| Priorité | Must (affichage) / Should (stratégie conflit EF-99) |
| Réf. | EF-98, EF-99 |

**Objectif :** EN_ATTENTE / ENVOYE / EN_CONFLIT. Serveur fait autorité ; version locale conservée et montrée ; jamais de résolution silencieuse.

**Acceptation**
- [x] Compteur « N éléments à envoyer ».
- [x] Conflit : bandeau explicite si `aqs.conflit` (stratégie complète en Should / L5).
- [ ] Version locale rejetée journalisée (Should).
