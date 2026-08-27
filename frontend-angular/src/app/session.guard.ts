import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionService } from './session.service';

export const sessionGuard: CanActivateFn = () => {
  const session = inject(SessionService);
  const router = inject(Router);
  if (!session.connecte()) {
    return router.parseUrl('/connexion');
  }
  if (session.doitChanger()) {
    return router.parseUrl('/mot-de-passe');
  }
  return true;
};

export const motDePasseGuard: CanActivateFn = () => {
  const session = inject(SessionService);
  const router = inject(Router);
  if (!session.connecte()) {
    return router.parseUrl('/connexion');
  }
  if (!session.doitChanger()) {
    return router.parseUrl(session.accueil());
  }
  return true;
};

export const rolesGuard: CanActivateFn = (route) => {
  const session = inject(SessionService);
  const router = inject(Router);
  const attendus = route.data['roles'] as string[] | undefined;
  if (!attendus || session.aUnDes(attendus)) {
    return true;
  }
  return router.parseUrl(session.accueil());
};
