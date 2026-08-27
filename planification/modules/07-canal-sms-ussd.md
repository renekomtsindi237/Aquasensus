# M7 — Canal SMS / USSD simulé

**Lot :** L4 · **Paquet :** `messaging`  
**Exigences :** EF-60 à EF-66  
**Diagrammes :** `UC7-canal-sms-ussd.puml`, `CL7-messagerie-simulee.puml`, `SQ3`, `SQ4`  
**Charte :** 160 car., GSM-7, pas d'émoji (§12.4)

Le métier ne connaît que le port `MessagingGateway`. Pas d'opérateur réel (bible).

---

## ISS-047 — Port MessagingGateway et simulateur

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L4 |
| Priorité | Must |
| Réf. | EF-60, DA-09 |

**Objectif :** Interface unique ; implémentation v1 = simulateur ; tests = adaptateur mémoire.

**Acceptation**
- [x] Le domaine ne dépend que de `MessagingGateway` ; le simulateur est un adaptateur.
- [x] Substitution : `aquasensus.messaging.adaptateur=simulateur`.

---

## ISS-048 — SMS entrant, accusé, journal

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L4 |
| Priorité | Must |
| Réf. | EF-61, EF-62, EF-65, CDC format `AQS <code> <symptôme>` |

**Objectif :** Analyse tolérante (casse, espaces). Échec → SMS d'aide. Succès → même règles métier que le web (corroboration).

**Acceptation**
- [x] `POST /api/v1/simulation/sms/inbound`.
- [x] Réponse ≤ 160 caractères, GSM-7.
- [x] Journal ENTRANT et SORTANT.
- [x] Téléphone haché en base (RG-10).

---

## ISS-049 — Session USSD arborescente

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L4 |
| Priorité | Must |
| Réf. | EF-63, annexe D du CDC |

**Objectif :** État serveur, expiration 90 s, jamais de reprise d'une session expirée.

**Acceptation**
- [x] `POST /api/v1/simulation/ussd/session`.
- [x] Menu : signaler / état / mes signalements (selon annexe).
- [x] Test d'expiration.

---

## ISS-050 — Console d'administration de simulation

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L4 |
| Priorité | Must |
| Réf. | EF-64 |

**Objectif :** Écran `ADMIN` : injecter SMS, ouvrir USSD, voir le journal.

**Acceptation**
- [x] `GET /api/v1/simulation/messages`.
- [x] Tokens de la charte ; logo par défaut (`/simulation`).

---

## ISS-051 — Notifications sortantes via le même port

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L4 |
| Priorité | Should |
| Réf. | EF-66 |

**Objectif :** Affectation, alerte, rétablissement passent par `MessagingGateway` (prépare M8).

**Acceptation**
- [x] Émission via `MessagingGateway` à l'affectation, à l'alerte et au rétablissement (`FinalisationV1ApiTest`).
