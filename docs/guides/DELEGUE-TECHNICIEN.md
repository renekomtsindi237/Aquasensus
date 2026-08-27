# Guide délégué / technicien (ISS-066)

Logo : `docs/design/aquasensus-logo.png` (copie PWA : `assets/brand/`).

## Délégué

1. Connexion (`delegue.a@aquasensus.local` en démo).
2. **File** : signalements à qualifier, interventions du comité.
3. **Carte** : chaque état a une **couleur, une forme et un libellé** (légende permanente).
4. **KPI** : temps de rétablissement ; les ouvrages `HORS_SERVICE` n'entrent pas dans la disponibilité.
5. Jamais de saisie de litres, bidons ou minutes de pompage.

## Technicien

1. Connexion (`tech.a@aquasensus.local` en démo).
2. Interventions affectées : diagnostic + action pour clôturer (le délégué confirme le rétablissement).
3. Hors ligne : bandeau + compteur « n éléments à envoyer » ; le serveur fait autorité en cas de conflit.

## Habitant (sans compte)

`/signaler` : code forage, symptôme, téléphone, code `123456` (OTP simulé). SMS simulé (admin) : `AQS CODE SYMPTOME`.
