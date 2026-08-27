import { Routes } from '@angular/router';
import { CarteComponent } from './carte/carte.component';
import { ConnexionComponent } from './connexion/connexion.component';
import { FileComponent } from './file/file.component';
import { KpiComponent } from './kpi/kpi.component';
import { PointsComponent } from './points/points.component';
import { SignalerComponent } from './signaler/signaler.component';
import { SimulationComponent } from './simulation/simulation.component';
import { ComptesComponent } from './comptes/comptes.component';
import { NotificationsComponent } from './notifications/notifications.component';
import { AdminReferentielsComponent } from './admin/admin-referentiels.component';
import { MotDePasseComponent } from './mot-de-passe/mot-de-passe.component';
import { LandingComponent } from './landing/landing.component';
import { AccueilComponent } from './accueil/accueil.component';
import { motDePasseGuard, rolesGuard, sessionGuard } from './session.guard';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'connexion', component: ConnexionComponent },
  { path: 'mot-de-passe', component: MotDePasseComponent, canActivate: [motDePasseGuard] },
  { path: 'accueil', component: AccueilComponent, canActivate: [sessionGuard] },
  { path: 'points', component: PointsComponent },
  { path: 'carte', component: CarteComponent },
  { path: 'signaler', component: SignalerComponent },
  {
    path: 'file',
    component: FileComponent,
    canActivate: [sessionGuard, rolesGuard],
    data: { roles: ['DELEGUE', 'ADMIN'] }
  },
  {
    path: 'kpi',
    component: KpiComponent,
    canActivate: [sessionGuard, rolesGuard],
    data: { roles: ['PARTENAIRE', 'DELEGUE', 'ADMIN'] }
  },
  { path: 'notifications', component: NotificationsComponent, canActivate: [sessionGuard] },
  {
    path: 'admin',
    component: AdminReferentielsComponent,
    canActivate: [sessionGuard, rolesGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'simulation',
    component: SimulationComponent,
    canActivate: [sessionGuard, rolesGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'comptes',
    component: ComptesComponent,
    canActivate: [sessionGuard, rolesGuard],
    data: { roles: ['ADMIN'] }
  }
];
