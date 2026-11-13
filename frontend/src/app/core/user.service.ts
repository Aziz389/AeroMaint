import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from './models';

/** Calls /api/users (SUPERVISOR and ADMIN only). */
@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);

  /** Technicians, to assign a ticket or filter the ticket list by assignee. */
  technicians(): Observable<User[]> {
    return this.http.get<User[]>('/api/users', { params: { role: 'TECHNICIAN' } });
  }
}
