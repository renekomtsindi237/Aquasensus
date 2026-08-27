# M8 — Notifications

**Lot :** L4 · **Priorité module :** Should  
**Exigences :** EF-70 à EF-73  
**Événements :** signalement grave, alerte émise, intervention affectée, intervention en retard, rétablissement confirmé

---

## ISS-052 — File d'événements, une notification par destinataire

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L4 |
| Priorité | Should |
| Réf. | EF-70, EF-73 |

**Objectif :** Publication après commit de transaction (TR-3). Reprise si échec d'envoi. Pas de doublon événement × destinataire.

**Acceptation**
- [x] Rollback métier → aucune notif envoyée.
- [x] Relance plafonnée, statut EN_ATTENTE / ENVOYEE / ECHOUEE.

---

## ISS-053 — Canaux in-app, SMS simulé, e-mail optionnel

| Champ | Valeur |
| --- | --- |
| Statut | Fait (in-app + SMS) |
| Lot | L4 |
| Priorité | Should (in-app + SMS) / Could (préférences EF-72, e-mail) |
| Réf. | EF-71, EF-72 |

**Objectif :** In-app obligatoire pour délégué/technicien connectés. SMS via le simulateur. Préférences utilisateur si le lot tient.

**Acceptation**
- [ ] Coupure des non-critiques (Could / EF-72).
- [x] In-app + SMS simulé (`GET /api/v1/notifications`).
