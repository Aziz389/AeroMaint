import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Role } from './models';

/** Route guard: only logged-in users may open the page, others go to /login. */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.isLoggedIn() ? true : router.createUrlTree(['/login']);
};

/**
 * Route guard: only users whose role is in the route's data.roles may open the page.
 * Example: { path: 'tickets/new', canActivate: [roleGuard], data: { roles: ['SUPERVISOR'] } }
 * This only hides pages in the browser; the real protection is @PreAuthorize on the backend.
 */
export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const allowedRoles = route.data['roles'] as Role[];
  return auth.hasRole(...allowedRoles) ? true : router.createUrlTree(['/tickets']);
};
