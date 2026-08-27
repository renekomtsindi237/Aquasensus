# Accessibilité (ISS-063)

- `lang="fr"` sur `index.html`
- Lien d'évitement « Aller au contenu »
- Navigation clavier : cibles ≥ 48 px, `:focus-visible` via token `--aqs-color-focus-ring`
- Six états : couleur **et** forme **et** libellé (légende `/carte`)
- SMS : GSM-7, pas d'émoji
- Contraste : tokens charte (revue manuelle des 6 états le 2026-08-26 sur `/carte`)
- axe-core : spec Karma `accessibilite.spec.ts` (règle contrast désactivée sans CSS global ; parcours signalement / file / liste ont des labels)
