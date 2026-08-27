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

export const routes: Routes = [
  { path: '', component: ConnexionComponent },
  { path: 'points', component: PointsComponent },
  { path: 'carte', component: CarteComponent },
  { path: 'signaler', component: SignalerComponent },
  { path: 'file', component: FileComponent },
  { path: 'kpi', component: KpiComponent },
  { path: 'notifications', component: NotificationsComponent },
  { path: 'admin', component: AdminReferentielsComponent },
  { path: 'simulation', component: SimulationComponent },
  { path: 'comptes', component: ComptesComponent }
];
