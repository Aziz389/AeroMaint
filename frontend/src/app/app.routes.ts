import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth.guards';
import { Login } from './pages/login/login';
import { Shell } from './layout/shell';

/**
 * Pages are lazy-loaded (loadComponent): their code is only downloaded when first opened.
 * authGuard protects everything inside the Shell; roleGuard restricts some pages to certain roles.
 */
export const routes: Routes = [
  { path: 'login', component: Login },
  {
    path: '',
    component: Shell,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'tickets' },

      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard').then((m) => m.Dashboard),
        canActivate: [roleGuard],
        data: { roles: ['SUPERVISOR', 'ADMIN'] },
      },
      {
        path: 'maintenance',
        loadComponent: () => import('./pages/maintenance/maintenance-plans').then((m) => m.MaintenancePlans),
        canActivate: [roleGuard],
        data: { roles: ['SUPERVISOR', 'ADMIN'] },
      },
      {
        path: 'audit-log',
        loadComponent: () => import('./pages/audit/audit-log').then((m) => m.AuditLog),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },

      { path: 'tickets', loadComponent: () => import('./pages/tickets/ticket-list').then((m) => m.TicketList) },
      {
        path: 'tickets/new',
        loadComponent: () => import('./pages/tickets/ticket-form').then((m) => m.TicketForm),
        canActivate: [roleGuard],
        data: { roles: ['SUPERVISOR'] },
      },
      { path: 'tickets/:id', loadComponent: () => import('./pages/tickets/ticket-detail').then((m) => m.TicketDetail) },
      {
        path: 'tickets/:id/edit',
        loadComponent: () => import('./pages/tickets/ticket-form').then((m) => m.TicketForm),
        canActivate: [roleGuard],
        data: { roles: ['SUPERVISOR'] },
      },

      { path: 'equipment', loadComponent: () => import('./pages/equipment/equipment-list').then((m) => m.EquipmentList) },
      {
        path: 'equipment/new',
        loadComponent: () => import('./pages/equipment/equipment-form').then((m) => m.EquipmentForm),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      {
        path: 'equipment/:id',
        loadComponent: () => import('./pages/equipment/equipment-detail').then((m) => m.EquipmentDetail),
        canActivate: [roleGuard],
        data: { roles: ['SUPERVISOR', 'ADMIN'] },
      },
      {
        path: 'equipment/:id/edit',
        loadComponent: () => import('./pages/equipment/equipment-form').then((m) => m.EquipmentForm),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
