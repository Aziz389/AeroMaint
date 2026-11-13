import { HttpErrorResponse } from '@angular/common/http';

/**
 * Turns an HTTP error into a message for the user.
 * The backend always answers errors with { status, message, fieldErrors } (GlobalExceptionHandler).
 */
export function errorMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return 'Cannot reach the server. Is the backend running?';
    }
    const body = error.error;
    if (body?.fieldErrors) {
      const fields = Object.entries(body.fieldErrors as Record<string, string>)
        .map(([field, message]) => `${field}: ${message}`)
        .join(', ');
      return `${body.message}: ${fields}`;
    }
    if (body?.message) {
      return body.message;
    }
  }
  return 'Unexpected error';
}
