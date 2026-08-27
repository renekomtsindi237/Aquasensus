# M9 — Comptes, rôles et administration

**Lots :** L0 (auth déjà au socle) · L2 (gestion complète)  
**Paquet :** `identity`  
**Exigences :** EF-80 à EF-85 (EF-80, 83, 85 = ISS-004)

---

## ISS-054 — Créer, suspendre, réactiver un compte et son périmètre

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L0 (compte admin démo) puis L2 (écran admin) |
| Priorité | Must |
| Réf. | EF-81 |

**Objectif :** Rôles USAGER, DELEGUE, TECHNICIEN, PARTENAIRE, ADMIN + périmètre comité/localité.

**Acceptation**
- [x] `GET/POST/PATCH /api/v1/users` — ADMIN uniquement.
- [x] Compte créé par admin : `doitChangerMotDePasse` au login (EF-83).
- [x] Suspension révoque les sessions de rafraîchissement.

---

## ISS-055 — Réinitialisation de mot de passe par SMS simulé

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 (ou L4 si gateway pas prêt : file interne) |
| Priorité | Must |
| Réf. | EF-82 |

**Objectif :** Code à usage unique. Réponse identique que le compte existe ou non.

**Acceptation**
- [x] `POST /api/v1/auth/password/reset-request` → 202.
- [x] Pas d'énumération de comptes.
- [x] Code expiré rejeté (`IdentifiantsInvalidesException`).

---

## ISS-056 — Référentiels d'administration

| Champ | Valeur |
| --- | --- |
| Statut | Fait |
| Lot | L2 |
| Priorité | Should |
| Réf. | EF-84 |

**Objectif :** Localités, comités, symptômes (si extension), types de pièces.

**Acceptation**
- [x] Opérations journalisées (M10).
- [x] Suppression logique (comité `actif = false`).
