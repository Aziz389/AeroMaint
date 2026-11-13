import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Runs on every HTTP request made with HttpClient.
 * 1. Adds "Authorization: Bearer <token>" when the user is logged in.
 * 2. If the API answers 401 (token expired or invalid), logs the user out and shows the login page.
 */
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AuthService);
  const token = auth.token();

  const authorizedRequest = token
    ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : request;

  return next(authorizedRequest).pipe(
    catchError((error: HttpErrorResponse) => {
      const isLoginCall = request.url.endsWith('/api/auth/login');
      if (error.status === 401 && !isLoginCall) {
        auth.logout();
      }
      return throwError(() => error);
    }),
  );
};
