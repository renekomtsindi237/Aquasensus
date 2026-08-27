# Performance (ISS-062)

Cibles CDC : lecture P95 &lt; 400 ms, écriture &lt; 800 ms ; carte 500 marqueurs (ENF-04 / ENF-06).

## Automatisé (CI)

`CarteChargeApiTest` insère 500 ouvrages et appelle `GET /api/v1/water-points/map`. Budget **2 s** sur H2 (mémoire, pas un serveur de prod). Forme + libellé d'état sont présents dans la projection.

## Charge reproductible (k6)

Prérequis : API joignable (`http://localhost:8080` ou proxy `:80`).

```powershell
k6 run infra/charge/lecture.js
```

Le script frappe `/api/v1/health` et `/api/v1/water-points/map` avec 20 VU pendant 30 s (approximation 200 utilisateurs lissés : monter `VUS` / durée sur une machine dédiée). Seuil : taux d'erreur &lt; 1 %.

## Rapport

| Date | Environnement | Résultat |
| --- | --- | --- |
| 2026-08-26 | Tests H2 CI | 500 marqueurs, GET map &lt; 2 s (voir Surefire `CarteChargeApiTest`) |
| — | Prod / démo Docker | À coller après `k6 run` (p95, erreurs) |
