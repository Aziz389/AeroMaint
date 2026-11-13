import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginResponse, Role, User } from './models';

const TOKEN_KEY = 'aeromaint.token';
const USER_KEY = 'aeromaint.user';

/**
 * Keeps track of who is logged in.
 * The JWT and the user are saved in localStorage so a page refresh keeps the session.
 * currentUser is a signal: every component that reads it updates automatically on login/logout.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  readonly currentUser = signal<User | null>(this.restoreUser());
  readonly isLoggedIn = computed(() => this.currentUser() !== null);

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', { username, password }).pipe(
      tap((response) => {
        localStorage.setItem(TOKEN_KEY, response.token);
        localStorage.setItem(USER_KEY, JSON.stringify(response.user));
        this.currentUser.set(response.user);
      }),
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  hasRole(...roles: Role[]): boolean {
    const user = this.currentUser();
    return user !== null && roles.includes(user.role);
  }

  /** On page load: restore the user only if the saved token has not expired yet. */
  private restoreUser(): User | null {
    const token = localStorage.getItem(TOKEN_KEY);
    const user = localStorage.getItem(USER_KEY);
    if (!token || !user || this.isExpired(token)) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
      return null;
    }
    return JSON.parse(user) as User;
  }

  /**
   * A JWT is "header.payload.signature"; the payload is Base64URL JSON containing "exp" (seconds).
   * Reading it here is only for convenience: the backend still verifies the signature on every request.
   */
  private isExpired(token: string): boolean {
    try {
      const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const exp = JSON.parse(atob(payload)).exp as number;
      return exp * 1000 < Date.now();
    } catch {
      return true;
    }
  }
}
