# PWA Angular (ISS-006)

```powershell
cd frontend-angular
npm start
```

Ouvre `http://localhost:4200`. Le proxy envoie `/api` vers `http://localhost:8080`.

Les couleurs viennent uniquement de `src/assets/design/tokens.css` (copie de `docs/design/tokens.css`). Logo : `src/assets/brand/aquasensus-logo.png` (copie non modifiée).

Routes L4 : `/carte` (ISS-043, pas de Leaflet), `/kpi` (ISS-044), `/simulation` (ISS-050, rôle ADMIN). Manifeste + `public/sw.js` (ISS-058). Compteur de file locale (ISS-060).
