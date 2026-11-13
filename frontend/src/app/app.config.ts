import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { authInterceptor } from './core/auth.interceptor';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    // withComponentInputBinding: a route parameter like :id is passed to the component's "id" input.
    provideRouter(routes, withComponentInputBinding()),
    // Every HttpClient request goes through authInterceptor (adds the JWT, handles 401).
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
};
